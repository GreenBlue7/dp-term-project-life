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
 * <p>Life creates the clock and the universe and connects them
 * (Observer: the universe listens to the clock). It is also the client
 * of the Command pattern: menu items, clicks and clock ticks are all
 * turned into commands whose receiver is the clock or the universe.
 * Changes to the board go through one {@link CommandHistory}, so they
 * can be undone one at a time.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame
{
	private final Universe universe = new Universe();
	private final Clock clock = new Clock();
	private final CommandHistory history = new CommandHistory( 100 );

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

		clock.addClockListener( () -> history.execute( SnapshotCommand.step(universe) ) );
		universe.setClickListener( (column, row) ->
				history.execute( new ToggleCellCommand(universe, column, row) ) );

		addCommand( "Grid", "Clear",	() -> history.execute( SnapshotCommand.clear(universe) ) );
		addCommand( "Grid", "Undo",		() -> undo()	);
		addCommand( "Grid", "Load",					() -> load()					);
		addCommand( "Grid", "Store",				() -> store()					);
		addCommand( "Grid", "Exit",					() -> System.exit(0)			);

		addCommand( "Go", "Halt",					() -> clock.stop()				);
		addCommand( "Go", "Tick (Single Step)",		() -> clock.tick()				);
		addCommand( "Go", "Agonizing",				() -> clock.startTicking(500)	);
		addCommand( "Go", "Slow",					() -> clock.startTicking(150)	);
		addCommand( "Go", "Medium",					() -> clock.startTicking(70)	);
		addCommand( "Go", "Fast",					() -> clock.startTicking(30)	);

		pack();
		setVisible( true );
	}

	private void addCommand(String menu, String item, Command command) {
		MenuSite.addLine(this, menu, item, e -> command.execute());
	}

	/** Stops the clock first; otherwise the next tick would redo the step. */
	private void undo()
	{	clock.stop();
		history.undo();
	}

	/** The clock stops only after a file is chosen, as in the original. */
	private void load()
	{	try( InputStream in = new FileInputStream(
				Files.userSelected(".",".life","Life File","Load")) )
		{	clock.stop();
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
		{	clock.stop();
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
