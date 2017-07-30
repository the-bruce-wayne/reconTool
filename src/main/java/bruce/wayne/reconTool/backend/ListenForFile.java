package bruce.wayne.reconTool.backend;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import bruce.wayne.reconTool.backend.utils.LoggersLoader;
import bruce.wayne.reconTool.backend.utils.PropertiesLoader;
import bruce.wayne.reconTool.frontend.ApplicationWindow;

public class ListenForFile implements ActionListener
{
	private PropertiesLoader props;
	private ApplicationWindow jFrame;
	private File file1;
	private File file2;
	private int column1 = -1;
	private int column2 = -1;
	private ArrayList<String> heading1;
	private ArrayList<String> heading2;
	private FileHandler fh;
	private LoggersLoader logger;

	public ListenForFile(PropertiesLoader props, ApplicationWindow jFrame,
			LoggersLoader logger)
	{
		this.props = props;
		this.jFrame = jFrame;
		this.logger = logger;
		this.fh = new FileHandler(this.logger, this.props);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{

		String actionCommand = e.getActionCommand();
		if (props.getReconButtonTitle().equals(actionCommand))
		{
			System.out.println("recon button pressed");
			// check both files have been selected
			if (this.file1 == null || this.file2 == null)
				this.jFrame.showDialogueBox(JOptionPane.ERROR_MESSAGE, "ERROR",
						"Please select 2 files to reconcile");

			else if (this.column1 == -1 || this.column2 == -1)
				this.jFrame.showDialogueBox(JOptionPane.ERROR_MESSAGE, "ERROR",
						"Please select 2 files to reconcile");

			else
			{
				try
				{
					this.fh.reconcile(this.file1, this.file2, this.column1,
							this.column2);
					File file = this.fh.getResults();
					this.jFrame.showDialogueBox(JOptionPane.INFORMATION_MESSAGE,
							"COMPLETE", "Result File: " + file.getAbsolutePath());

				} catch (EncryptedDocumentException e1)
				{
					this.jFrame.showDialogueBox(JOptionPane.ERROR_MESSAGE, "ERROR",
							"File: encrypted: " + e1.getMessage());
				} catch (InvalidFormatException e2)
				{
					this.jFrame.showDialogueBox(JOptionPane.ERROR_MESSAGE, "ERROR",
							"Invalide heading column number: " + e2.getMessage());
				} catch (IOException e3)
				{
					this.jFrame.showDialogueBox(JOptionPane.ERROR_MESSAGE, "ERROR",
							"File error: " + e3.getMessage());
				}
			}

		} else if ((props.getFileChoserButtonTitle() + " 1")
				.equals(actionCommand))
		{
			// get file for recon process
			System.out.println("About to choose file");
			JButton button = (JButton) e.getSource();
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				this.file1 = fileChooser.getSelectedFile();
				button.setText(this.file1.getName());
				try
				{
					this.heading1 = this.fh.getColumnHeading(this.file1,
							this.props.getDefaultHeadingRow());
					this.jFrame.updateComboBoxOne(this.heading1);
				} catch (IOException e1)
				{

				} catch (NumberFormatException e2)
				{

				}
			}
		} else if ((props.getFileChoserButtonTitle() + " 2")
				.equals(actionCommand))
		{
			// get file for recon process
			System.out.println("About to choose file");
			JButton button = (JButton) e.getSource();
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				this.file2 = fileChooser.getSelectedFile();
				button.setText(this.file2.getName());

				try
				{
					this.heading2 = this.fh.getColumnHeading(this.file2,
							this.props.getDefaultHeadingRow());
					this.jFrame.updateComboBoxTwo(this.heading2);
				} catch (IOException e1)
				{

				} catch (NumberFormatException e2)
				{

				}
			}

		} else if (e.getSource() == this.jFrame.columns1)
		{
			String id = this.jFrame.columns1.getSelectedItem().toString();
			this.column1 = this.heading1.indexOf(id);
		} else if (e.getSource() == this.jFrame.columns2)
		{
			String id = this.jFrame.columns2.getSelectedItem().toString();
			this.column2 = this.heading2.indexOf(id);
		}

	}

}
