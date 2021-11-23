package de.feu.propra.ui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	public MenuBar() {
		var fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem("Open..."));
		fileMenu.add(new JMenuItem("Reload"));
		fileMenu.add(new JMenuItem("Analyse Multiple Files..."));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem("Exit"));
		
		var toolsMenu = new JMenu("Tools");
		toolsMenu.add(new JMenuItem("Remove Token")); // TODO must reset the graph
		toolsMenu.add(new JMenuItem("Add Token")); // TODO must reset the graph
		toolsMenu.add(new JMenuItem("Reset"));
		toolsMenu.add(new JMenuItem("Delete Graph")); // TODO must also reset net
		toolsMenu.addSeparator();
		toolsMenu.add(new JMenuItem("Preferences..."));

		var helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem("Info..."));

		add(fileMenu);
		add(toolsMenu);
		add(helpMenu);
	}

}
