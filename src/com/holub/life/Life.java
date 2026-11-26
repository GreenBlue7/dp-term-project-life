package com.holub.life;

import java.awt.*;
import javax.swing.*;
import com.holub.ui.MenuSite;
import com.holub.life.command.Command;

/*******************************************************************
 * An implemenation of Conway's Game of Life.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame
{	
	private static JComponent universe;

	public static void main( String[] arguments )
	{	new Life();
	}

	private Life()
	{	super( "The Game of Life. "
					+"(c)2003 Allen I. Holub <http://www.holub.com>");

		// Must establish the MenuSite very early in case
		// a subcomponent puts menus on it.
		MenuSite.establish( this );		//{=life.java.establish}

		setDefaultCloseOperation	( EXIT_ON_CLOSE 		);
		getContentPane().setLayout	( new BorderLayout()	);
		getContentPane().add( Universe.instance(), BorderLayout.CENTER); //{=life.java.install}

		Clock clock = Clock.instance();
		clock.addClockListener(Universe.instance()::nextGeneration);

		addCommand( "Go", "Halt",				() -> clock.stop()				);
		addCommand( "Go", "Tick (Single Step)",	() -> clock.tick()				);
		addCommand( "Go", "Agonizing",			() -> clock.startTicking(500)	);
		addCommand( "Go", "Slow",				() -> clock.startTicking(150)	);
		addCommand( "Go", "Medium",				() -> clock.startTicking(70)	);
		addCommand( "Go", "Fast",				() -> clock.startTicking(30)	);
		pack();
		setVisible( true );
	}

	private void addCommand(String menu, String item, Command command) {
		MenuSite.addLine(this, menu, item, e -> command.execute());
	}
}
