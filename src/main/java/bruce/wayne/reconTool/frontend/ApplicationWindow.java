package bruce.wayne.reconTool.frontend;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import bruce.wayne.reconTool.backend.ListenForFile;
import bruce.wayne.reconTool.backend.utils.LoggersLoader;
import bruce.wayne.reconTool.backend.utils.PropertiesLoader;

public class ApplicationWindow extends JFrame
{
	/**
	 * to fix serialisable class warning
	 */
	private static final long serialVersionUID = 8695920898179762362L;
	
	// properties with applications settings
	private PropertiesLoader props;
	// logger 
	LoggersLoader logger;
	private ListenForFile listener;
	JPanel mainPanel;
	JPanel subPanel;
	JPanel eastPanel;
	JPanel westPanel;
	JButton reconButton;
	public JComboBox<String> columns1;
	public JComboBox<String> columns2;

	public ApplicationWindow()
	{
		try
		{
			// load properties from file
			props = new PropertiesLoader();
			props.load();

			// start logger
			this.logger = new LoggersLoader();
		} catch (IOException e)
		{
			this.showDialogueBox(JOptionPane.ERROR_MESSAGE, "ERROR",
					"Failed to load properties File: " + e.getMessage());
			System.exit(0);
		}
		
		this.listener = new ListenForFile(props, this, this.logger);
		columns1 = new JComboBox<String>();
		columns2 = new JComboBox<String>();

	}

	// load logger
	public void intilaiseUI()
	{
		// reconcilisation button
		reconButton = new JButton(props.getReconButtonTitle());
		reconButton.addActionListener(this.listener);
		// componets of west panel
		westPanel = this.getPanel(props.getFileChoserButtonTitle() + " 1", this.columns1);
		// componets of east panel
		eastPanel = this.getPanel(props.getFileChoserButtonTitle() + " 2", this.columns2);
		// componets of east and west panels
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 2));
		subPanel.add(westPanel);
		subPanel.add(eastPanel);
		// componets of main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(subPanel);
		mainPanel.add(reconButton, BorderLayout.SOUTH);

		// window title
		this.setTitle(props.getTitle());

		// window dimensions
		// TODO handle number NumberFormatException Exception
		this.setSize(Integer.parseInt(props.getWindowWidth()),
				Integer.parseInt(props.getWindowLength()));

		// place window in relatice centre
		this.setLocationRelativeTo(null);

		// close application on exit botton
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// add main panel to window
		this.add(mainPanel);
		this.setVisible(true);

	}

	private JPanel getPanel(String fileChooserName, JComboBox<String> columns)
	{
		JPanel panel = new JPanel();
		columns.addActionListener(this.listener);
		JButton fileChooserButton = new JButton(fileChooserName);
		fileChooserButton.addActionListener(this.listener);

		panel.setLayout(new BorderLayout());
		panel.add(columns, BorderLayout.NORTH);
		panel.add(fileChooserButton, BorderLayout.CENTER);
		return panel;
	}
	
	public JFileChooser showFileChooserBox()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(props.getCurrentDirectory()));
		fileChooser.setDialogTitle(props.getFileChooserDialogeTitle());
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
		props.getFileNameExtensionFilterTitle(), "xlsx", "xls", "csv"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		
		return fileChooser;	
	}

	public void showDialogueBox(int messageType, String title, String message)
	{
		JOptionPane.showMessageDialog(this, message, title, messageType);
	}
	
	public void updateComboBoxOne(ArrayList<String> headings)
	{
		for(String heading: headings)
			this.columns1.addItem(heading);
	}
	
	public void updateComboBoxTwo(ArrayList<String> headings)
	{
		for(String heading: headings)
			this.columns2.addItem(heading);
	}
	
	

}