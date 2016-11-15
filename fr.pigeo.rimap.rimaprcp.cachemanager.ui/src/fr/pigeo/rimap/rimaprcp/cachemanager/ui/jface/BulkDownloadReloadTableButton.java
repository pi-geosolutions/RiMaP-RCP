 
package fr.pigeo.rimap.rimaprcp.cachemanager.ui.jface;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;

public class BulkDownloadReloadTableButton {
	@Execute
	public void execute(IEventBroker evtBroker) {
		evtBroker.post(CacheManagerEventConstants.BULKDOWNLOAD_TABLE_RELOAD, null);
	}
		
}