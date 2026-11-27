package com.holub.life;

import java.io.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.holub.life.Cell;
import com.holub.life.Storable;
import com.holub.life.Neighborhood;
import com.holub.life.Resident;

/**
 * The Universe sits between the Swing event model and the Life classes:
 * it turns mouse clicks and paint requests into calls on the outermost
 * Neighborhood, which it also creates. Load and store go through the
 * grid's Memento, and Universe never looks inside it.
 *
 * <p>It used to be a Singleton that installed its own menus and found
 * the clock by itself. {@link Life} now creates it and wires it up.
 *
 * <p>Pattern: Mediator between Swing and the cells. Also the receiver
 * of the board commands.
 *
 * @include /etc/license.txt
 */

public class Universe extends JPanel
{	private 		final Cell  	outermostCell;
	private 		ClickListener	clickListener = this::toggle;
	private 		final RuleSlot	rules = new RuleSlot();

	/** The default height and width of a Neighborhood in cells.
	 *  If it's too big, you'll run too slowly because
	 *  you have to update the entire block as a unit, so there's more
	 *  to do. If it's too small, you have too many blocks to check.
	 *  I've found that 8 is a good compromise.
	 */
	private static final int  DEFAULT_GRID_SIZE = 8;

	/** The size of the smallest "atomic" cell---a Resident object.
	 *  This size is extrinsic to a Resident (It's passed into the
	 *  Resident's "draw yourself" method.
	 */
	private static final int  DEFAULT_CELL_SIZE = 8;

	public Universe()
	{	// Create the nested Cells that comprise the "universe." A bug
		// in the current implementation causes the program to fail
		// miserably if the overall size of the grid is too big to fit
		// on the screen.

		outermostCell = new Neighborhood
						(	DEFAULT_GRID_SIZE,
							new Neighborhood
							(	DEFAULT_GRID_SIZE,
								new Resident( rules )
							)
						);

		final Dimension PREFERRED_SIZE =
						new Dimension
						(  outermostCell.widthInCells() * DEFAULT_CELL_SIZE,
						   outermostCell.widthInCells() * DEFAULT_CELL_SIZE
						);

		addComponentListener
		(	new ComponentAdapter()
			{	public void componentResized(ComponentEvent e)
				{
					// Make sure that the cells fit evenly into the
					// total grid size so that each cell will be the
					// same size. For example, in a 64x64 grid, the
					// total size must be an even multiple of 63.

					Rectangle bounds = getBounds();
					bounds.height /= outermostCell.widthInCells();
					bounds.height *= outermostCell.widthInCells();
					bounds.width  =  bounds.height;
					setBounds( bounds );
				}
			}
		);

		setBackground	( Color.white	 );
		setPreferredSize( PREFERRED_SIZE );
		setMaximumSize	( PREFERRED_SIZE );
		setMinimumSize	( PREFERRED_SIZE );
		setOpaque		( true			 );

		addMouseListener					//{=Universe.mouse}
		(	new MouseAdapter()
			{	public void mousePressed(MouseEvent e)
				{	int width			= outermostCell.widthInCells();
					int pixelsPerCell	= getWidth() / width;
					if( pixelsPerCell == 0 )
						return;

					int column	= e.getX() / pixelsPerCell;
					int row		= e.getY() / pixelsPerCell;
					if( column < width && row < width )
						clickListener.cellClicked( column, row );
				}
			}
		);
	}

	/** Receives the cell the user clicked, in board coordinates. */
	public interface ClickListener
	{	void cellClicked( int column, int row );
	}

	/** Decides what a click does. By default it toggles the cell. */
	public void setClickListener( ClickListener listener )
	{	clickListener = listener;
	}

	/** Flips one cell between alive and dead. Toggling twice restores it. */
	public void toggle( int column, int row )
	{	int width = outermostCell.widthInCells();
		outermostCell.userClicked( new Point(column, row),
								   new Rectangle(0, 0, width, width) );
		repaint();
	}

	/** Advances the grid by one generation and repaints what changed.
	 *  @return true if any cell changed.
	 */
	public boolean nextGeneration() {
		boolean changed = outermostCell.figureNextState(
				Cell.DUMMY, Cell.DUMMY, Cell.DUMMY, Cell.DUMMY,
				Cell.DUMMY, Cell.DUMMY, Cell.DUMMY, Cell.DUMMY)
			&& outermostCell.transition();
		if (changed) refreshNow();
		return changed;
	}

	/** Captures the board in a Memento that only the grid can read. */
	Storable snapshot()
	{	return outermostCell.createMemento();
	}

	/** Puts back a board captured by {@link #snapshot()} or read from a file. */
	void restore( Storable memento )
	{	outermostCell.clear();
		outermostCell.transfer( memento, new Point(0,0), Cell.LOAD );
		repaint();
	}

	/** Kills every cell on the grid. */
	public void clear()
	{	outermostCell.clear();
		repaint();
	}

	/** Replaces the grid with the cells read from <code>in</code>.
	 *  The board is left as it was if the file cannot be read.
	 */
	public void load( InputStream in ) throws IOException
	{	Storable memento = outermostCell.createMemento();
		memento.load( in );
		restore( memento );
	}

	/** Writes the live cells to <code>out</code>.
	 *  createMemento() already records every live cell, so no further transfer is needed.
	 */
	public void store( OutputStream out ) throws IOException
	{	outermostCell.createMemento().flush( out );
	}

	/** Override paint to ask the outermost Neighborhood
	 *  (and any subcells) to draw themselves recursively.
	 *  All knowledge of screen size is also encapsulated.
	 *  (The size is passed into the outermost <code>Cell</code>.)
	 */

	public void paint(Graphics g)
	{
		Rectangle panelBounds = getBounds();
		Rectangle clipBounds  = g.getClipBounds();

		// The panel bounds is relative to the upper-left
		// corner of the screen. Pretend that it's at (0,0)
		panelBounds.x = 0;
		panelBounds.y = 0;
		outermostCell.redraw(g, panelBounds, true);		//{=Universe.redraw1}
	}

	/** Force a screen refresh by queing a request on
	 *  the Swing event queue. This is an example of the
	 *  Active Object pattern (not covered by the Gang of Four).
	 *  This method is called on every clock tick. Note that
	 *  the redraw() method on a given <code>Cell</code>
	 *  does nothing if the <code>Cell</code> doesn't
	 *  have to be refreshed.
	 */

	private void refreshNow()
	{	SwingUtilities.invokeLater
		(	new Runnable()
			{	public void run()
				{	Graphics g = getGraphics();
					if( g == null )		// Universe not displayable
						return;
					try
					{
						Rectangle panelBounds = getBounds();
						panelBounds.x = 0;
						panelBounds.y = 0;
						outermostCell.redraw(g, panelBounds, false); //{=Universe.redraw2}
					}
					finally
					{	g.dispose();
					}
				}
			}
		);
	}
}
