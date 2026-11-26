package com.holub.life;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import com.holub.io.Files;
import com.holub.ui.MenuSite;
import com.holub.life.command.Command;

/*******************************************************************
 * An implementation of Conway's Game of Life.
 *
 * <p>Life creates the clock and the universe and connects them
 * [[Observer: the universe listens to the clock]]. It is also the client
 * of the Command pattern: it builds every menu item from a command whose
 * receiver is the clock or the universe. The order of the menus is
 * decided here, not by the order in which objects happen to be created.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame
{
	private final Universe universe = new Universe();
	private final Clock clock = new Clock();

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

		clock.addClockListener( universe::nextGeneration );

		addCommand( "Grid", "Clear",				() -> universe.clear()			);
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

	/** The clock stops only after a file is chosen, as in the original. */
	private void load()
	{	try( InputStream in = new FileInputStream(
				Files.userSelected(".",".life","Life File","Load")) )
		{	clock.stop();
			universe.load( in );
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
