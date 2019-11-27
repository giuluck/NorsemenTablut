package it.unibo.ai.didattica.competition.tablut.gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;


public class BackgroundBrandub extends Background{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public BackgroundBrandub()
	{
		super();
		try
		{
			InputStream input = Gui.class.getResourceAsStream("images/brandub1.png");
			super.background = ImageIO.read(input);
			super.background = super.background.getScaledInstance(283,292,Image.SCALE_DEFAULT);
			input = Gui.class.getResourceAsStream("images/black3.png");
			super.black = ImageIO.read(input);
			super.black = super.black.getScaledInstance(34, 34, Image.SCALE_DEFAULT);
			input = Gui.class.getResourceAsStream("images/White1.png");
			super.white = ImageIO.read(input);
			super.white = super.white.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
			input = Gui.class.getResourceAsStream("images/ImmagineRe.png");
			super.king = ImageIO.read(input);
			super.king = super.king.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		}
		catch(IOException ie)
		{
			//System.out.println(ie.getMessage());
		}
	}
}
