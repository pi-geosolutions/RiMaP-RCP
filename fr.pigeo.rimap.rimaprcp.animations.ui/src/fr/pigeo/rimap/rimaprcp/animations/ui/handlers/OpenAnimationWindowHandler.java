
package fr.pigeo.rimap.rimaprcp.animations.ui.handlers;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fr.pigeo.rimap.rimaprcp.animations.core.Animations;

public class OpenAnimationWindowHandler {

	private AnimationsDialog ad;

	@Execute
	public void execute(Shell shell, IEclipseContext context, final Animations an, Display display) {
		if (ad == null) {
			ad = new AnimationsDialog(shell);
			context.set(Animations.class, an);
			ContextInjectionFactory.inject(ad, context);
		}
		
		BusyIndicator.showWhile(display, new Runnable() {
			@Override
			public void run() {
				an.load();
			}
		});

		ad.open();
	}

}