 
package fr.pigeo.rimap.rimaprcp.menus;

import org.eclipse.e4.core.di.annotations.Execute;

import fr.pigeo.rimap.rimaprcp.catalog.RiskCatalogImpl;

public class load {
	@Execute
	public void execute() {
		RiskCatalogImpl catalog = new RiskCatalogImpl("http://ne-risk.pigeo.fr/ne-risk-gn2_10/srv/fre/pigeo.layertree.get");
				System.out.println("loading...");
	}
		
}