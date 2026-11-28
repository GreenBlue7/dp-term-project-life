package com.holub.life;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import com.holub.io.Files;
import com.holub.ui.MenuSite;
import com.holub.life.command.Command;
import com.holub.life.command.CommandHistory;

/*******************************************************************
 * An implementation of Conway's Game of Life.
 *
 * <p>Life creates the clock and the universe and connects them, so the
 * universe moves on every tick. Menu items, clicks and clock ticks are
 * all turned into commands whose receiver is the clock or the universe.
 * Changes to the board go through one {@link CommandHistory}, so they
 * can be undone one at a time.
 *
 * <p>Pattern: Command. Life is the client. It also wires the Observer.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame
{
	private final Universe universe = new Universe();
	private final Clock clock = new Clock();
	private final CommandHistory history = new CommandHistory( 100 );
	private final Player player = new Player(clock, history, universe);

	public static void main( String[] arguments )
	{	new Life();
	}

	private Life()
	{	super( "The Game of Life. "
					+"(c)2003 Allen I. Holub <http://www.holub.com>");

		MenuSite.establish( this );		//{=life.java.establish}

		setDefaultCloseOperation	( EXIT_ON_CLOSE 		);
		getContentPane().setLayout	( new BorderLayout()	);
		getContentPane().add( universe, BorderLayout.CENTER); //{=life.java.install}
		getContentPane().add( controls(), BorderLayout.SOUTH );

		universe.setClickListener( (column, row) ->
				history.execute( new ToggleCellCommand(universe, column, row) ) );

		addCommand( "Grid", "Clear",	() -> history.execute( SnapshotCommand.clear(universe) ) );
		addCommand( "Grid", "Undo",		() -> player.stepBack()	);
		addCommand( "Grid", "Load",					() -> load()					);
		addCommand( "Grid", "Store",				() -> store()					);
		addCommand( "Grid", "Exit",					() -> System.exit(0)			);

		addCommand( "Go", "Halt",					() -> player.pause()				);
		addCommand( "Go", "Tick (Single Step)",		() -> player.stepForward()				);
		addCommand( "Go", "Agonizing",				() -> player.playAt(Player.AGONIZING)	);
		addCommand( "Go", "Slow",					() -> player.playAt(Player.SLOW)	);
		addCommand( "Go", "Medium",					() -> player.playAt(Player.MEDIUM)	);
		addCommand( "Go", "Fast",					() -> player.playAt(Player.FAST)	);

		addRule( "Conway",		LifeRule.CONWAY		);
		addRule( "HighLife",	LifeRule.HIGHLIFE	);
		addRule( "Seeds",		LifeRule.SEEDS		);


		pack();
		setVisible( true );
	}

	private void addCommand(String menu, String item, Command command) {
		MenuSite.addLine(this, menu, item, e -> command.execute());
	}

	private void addRule(String label, LifeRule rule){
		addCommand("Rules", label, () -> history.execute(() -> universe.setRule(rule)));
	}

	/** The << < > >> buttons under the board, with the player's state. */
	private JComponent controls()
	{	JLabel status = new JLabel( player.status() );
		player.setOnChange( () ->
				SwingUtilities.invokeLater( () -> status.setText( player.status() ) ) );

		JPanel bar = new JPanel();
		bar.add( button( "<<",	() -> player.rewind()		) );
		bar.add( button( "<",	() -> player.stepBack()		) );
		bar.add( button( ">",	() -> player.stepForward()	) );
		bar.add( button( ">>",	() -> player.play()			) );
		bar.add( status );
		return bar;
	}

	private JButton button( String label, Command command )
	{	JButton button = new JButton( label );
		button.addActionListener( e -> command.execute() );
		return button;
	}

	/** The clock stops only after a file is chosen, as in the original. */
	private void load()
	{	try( InputStream in = new FileInputStream(
				Files.userSelected(".",".life","Life File","Load")) )
		{	player.pause();
			universe.load( in );
			history.clear();		// the old commands no longer match the board
		}
		catch( IOException e )
		{	showError( "Read Failed!" );
		}
	}

	private void store()
	{	try( OutputStream out = new FileOutputStream(
				Files.userSelected(".",".life","Life File","Write")) )
		{	player.pause();
			universe.store( out );
		}
		catch( IOException e )
		{	showError( "Write Failed!" );
		}
	}

	private void showError( String message )
	{	JOptionPane.showMessageDialog( this, message,
				"The Game of Life", JOptionPane.ERROR_MESSAGE );
	}
}
