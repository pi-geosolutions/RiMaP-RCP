package fr.pigeo.rimap.rimaprcp.cachemanager.ui.utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.ProgressBar;

public class GlobalProgressMonitor extends NullProgressMonitor {
	// thread-Safe via thread confinement of the UI-Thread 
  // (means access only via UI-Thread)
    private long runningTasks = 0L;
    

    protected UISynchronize sync;
	protected ProgressBar progressBar;

	public GlobalProgressMonitor(UISynchronize sync,ProgressBar progressBar) {
		super();
		this.sync = sync;
		this.progressBar = progressBar;
	}

    
    @Override
    public void beginTask(final String name, final int totalWork) {
      sync.syncExec(new Runnable() {
        
        @Override
        public void run() {
          if(runningTasks <= 0) {
            // --- no task is running at the moment ---
            progressBar.setSelection(0);
            progressBar.setMaximum(totalWork);
            
          } else {
            // --- other tasks are running ---
            progressBar.setMaximum(progressBar.getMaximum() + totalWork);
          }
          
          runningTasks++;
          progressBar.setToolTipText("Currently running: " + runningTasks + 
              "\nLast task: " + name);
        }
      });
    }
  
    @Override
    public void worked(final int work) {
      sync.syncExec(new Runnable() {
  
        @Override
        public void run() {
          progressBar.setSelection(progressBar.getSelection() + work);
        }
      });
    }
    
    public IProgressMonitor addJob(Job job){
      if(job != null){
        job.addJobChangeListener(new JobChangeAdapter() {
          @Override
          public void done(IJobChangeEvent event) {
            sync.syncExec(new Runnable() {
              
              @Override
              public void run() {
                runningTasks--;
                if (runningTasks > 0){
                  // --- some tasks are still running ---
                  progressBar.setToolTipText("Currently running: " + runningTasks);
                  
                } else {
                  // --- all tasks are done (a reset of selection could also be done) ---
                  progressBar.setToolTipText("No background progress running.");
                }
              } });
            
            // clean-up
            event.getJob().removeJobChangeListener(this);
          }
        });
      }
      return this;
    }
  }
