package clientFiles;

import javafx.scene.control.TextArea;

public interface Updater
{
	public TextArea centerText = null;

	public void toUpdate(String newText);
}
