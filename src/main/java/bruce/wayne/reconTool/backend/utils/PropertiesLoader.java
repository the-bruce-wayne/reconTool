package bruce.wayne.reconTool.backend.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader
{
	private static final String PROPERTIES_FILE = "config.properties";
	private String saveAsName;
	private String saveAsExtension;
	private String emptyFileMessage;
	private String title;
	private String windowLength;
	private String windowWidth;
	private String fileChooserDialogeTitle;
	private String reconButtonTitle;
	private String fileNameExtensionFilterTitle;
	private String fileChoserButtonTitle;
	private String currentDirectory;
	private String defaultHeadingRow;
	
	public void load() throws FileNotFoundException, IOException
	{
		Properties prop = new Properties();

		InputStream input = new FileInputStream(PROPERTIES_FILE);

			// load properties file
			prop.load(input);
			
			// get the property value
			this.saveAsName = prop.getProperty("saveAsName");
			this.saveAsExtension = prop.getProperty("saveAsExtension");
			this.emptyFileMessage = prop.getProperty("emptyFileMessage");
			this.title = prop.getProperty("title");
			this.windowLength = prop.getProperty("windowLength");
			this.windowWidth = prop.getProperty("windowWidth");
			this.fileChooserDialogeTitle = prop.getProperty("fileChooserDialogeTitle");
			this.reconButtonTitle = prop.getProperty("reconButtonTitle");
			this.fileNameExtensionFilterTitle = prop.getProperty("fileNameExtensionFilterTitle");
			this.fileChoserButtonTitle = prop.getProperty("fileChoserButtonTitle");
			this.currentDirectory = prop.getProperty("currentDirectory");
			this.defaultHeadingRow = prop.getProperty("defaultHeadingRow");
	}
	
	public String getSaveAsName()
	{
		return this.saveAsName;
	}
	
	public String getSaveAsExtension()
	{
		return this.saveAsExtension;
	}

	public String getEmptyFileMessage()
	{
		return this.emptyFileMessage;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public String getWindowLength()
	{
		return this.windowLength;
	}
	
	public String getWindowWidth()
	{
		return this.windowWidth;
	}
	
	public String getFileChooserDialogeTitle()
	{
		return this.fileChooserDialogeTitle;
	}
	
	public String getReconButtonTitle()
	{
		return this.reconButtonTitle;
	}

	public String getFileNameExtensionFilterTitle()
	{
		return this.fileNameExtensionFilterTitle;
	}
	
	public String getFileChoserButtonTitle()
	{
		return this.fileChoserButtonTitle;
	}
	
	public String getCurrentDirectory()
	{
		return this.currentDirectory;
	}

	public int getDefaultHeadingRow()
	{
		return Integer.parseInt(this.defaultHeadingRow);
	}
	
}
