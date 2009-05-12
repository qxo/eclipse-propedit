package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.jdt.proposal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.swt.widgets.Shell;

public class PropertiesCompletionProposalComputer implements
		IJavaCompletionProposalComputer {

	public List computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		if (!(context instanceof JavaContentAssistInvocationContext)) {
			return Collections.EMPTY_LIST;
		}
		
		JavaContentAssistInvocationContext jContext = (JavaContentAssistInvocationContext)context;
		IJavaProject jProject = jContext.getProject();
		IProject project = jProject.getProject();
		
		IPath outputPath = null;
		try {
			outputPath = jProject.getOutputLocation();
		} catch (JavaModelException e) {
			IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
			ILog log = PropertiesEditorPlugin.getDefault().getLog();
			log.log(status);
		}

		class GetPropertyFiles implements IRunnableWithProgress {
			
			private IFile[] files = null;
			private IProject project = null;
			private IPath excludePath = null;
			
			public GetPropertyFiles(IProject project, IPath excludePath) {
				this.project = project;
				this.excludePath = excludePath;
			}
			
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.setTaskName("searching property");
				files = findFileExt(project, excludePath, "properties");
				monitor.done();
			}
			
			public IFile[] getResult() {
				return this.files;
			}
			
		}

		Shell shell = PropertiesEditorPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		GetPropertyFiles getPropFiles = new GetPropertyFiles(project, outputPath);
		try {
			dialog.run(true, false, getPropFiles);
		} catch (InvocationTargetException e) {
			IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
			ILog log = PropertiesEditorPlugin.getDefault().getLog();
			log.log(status);
		} catch (InterruptedException e) {
		}
		IFile[] pFiles = getPropFiles.getResult();
		
		List keyList = new ArrayList();
		for (int i = 0; i < pFiles.length; i++) {
			Properties prop = new Properties();
			try {
				prop.load(pFiles[i].getContents());
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
				ILog log = PropertiesEditorPlugin.getDefault().getLog();
				log.log(status);
			} catch (CoreException e) {
				IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
				ILog log = PropertiesEditorPlugin.getDefault().getLog();
				log.log(status);
			}
			Iterator ite = prop.keySet().iterator();
			while (ite.hasNext()) {
				String key = (String)ite.next();
				if (keyList.contains(key)) {
					continue;
				} else {
					keyList.addAll(prop.keySet());
				}
			}
		}
		
		String source = context.getDocument().get();
		int offset = context.getInvocationOffset();
		int idx = source.charAt(offset) == '\"' ? source.lastIndexOf("\"", offset - 1) : source.lastIndexOf("\"", offset);
		StringBuffer buf = new StringBuffer();
		for (int i = idx + 1; i < offset; i++) {
			char c = source.charAt(i);
			buf.append(c);
		}
		
		String match = buf.toString();
		
		List list = new ArrayList();
		
		Collections.sort(keyList);
		Iterator ite = keyList.iterator();
		while (ite.hasNext()) {
			String key = (String)ite.next();
			if (key.startsWith(match)) {
				list.add(new CompletionProposal(key, offset - match.length(),
						match.length(), key.length()));
			}
		}
		
		return list;
	}

	public List computeContextInformation(ContentAssistInvocationContext arg0,
			IProgressMonitor arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage() {
		return "An error happened when searching the property.";
	}

	public void sessionEnded() {
	}

	public void sessionStarted() {
	}
	
	public IFile[] findFileExt(IContainer container, IPath excludePath, String extension) {
		IResource[] list = null;
		try {
			list = container.members();
		} catch (CoreException e) {
			IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
			ILog log = PropertiesEditorPlugin.getDefault().getLog();
			log.log(status);
			return new IFile[0];
		}
		if (list == null) {
			return new IFile[0];
		}
		List fileList = new ArrayList();
		for (int i = 0; i < list.length; i++) {
			if (list[i] instanceof IFile) {
				if (extension.equals(list[i].getFileExtension())) {
					fileList.add((IFile)list[i]);
				}
			} else 	if (list[i] instanceof IContainer) {
				if (excludePath.matchingFirstSegments(list[i].getFullPath()) == excludePath.segmentCount()) {
					continue;
				}
				IFile[] files = findFileExt((IContainer)list[i], excludePath, extension);
				for (int j = 0; j < files.length; j++) {
					fileList.add(files[j]);
				}
			}
		}
		
		return (IFile[])fileList.toArray(new IFile[0]);
	}

}
