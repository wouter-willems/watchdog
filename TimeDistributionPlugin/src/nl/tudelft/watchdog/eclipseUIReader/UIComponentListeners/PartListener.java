package nl.tudelft.watchdog.eclipseUIReader.UIComponentListeners;
import nl.tudelft.watchdog.eclipseUIReader.DocChangeListenerAttacher;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentActivateEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentDeActivateEvent;
import nl.tudelft.watchdog.eclipseUIReader.Events.DocumentNotifier;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;


public class PartListener implements IPartListener{
	@Override
	public void partOpened(IWorkbenchPart part) {
		
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if(part instanceof ITextEditor)
		{
			DocumentNotifier.fireDocumentEndFocusEvent(new DocumentDeActivateEvent(part));
		}
	}
	
	@Override
	public void partClosed(IWorkbenchPart part) {
		if(part instanceof ITextEditor)
		{
			DocumentNotifier.fireDocumentStopEditingEvent(new DocumentDeActivateEvent(part));
		}
	}
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		if(part instanceof ITextEditor)
		{
			DocumentNotifier.fireDocumentStartFocusEvent(new DocumentActivateEvent(part));
			DocChangeListenerAttacher.listenToDocChanges(part);			
		}
	}

	
	
}
