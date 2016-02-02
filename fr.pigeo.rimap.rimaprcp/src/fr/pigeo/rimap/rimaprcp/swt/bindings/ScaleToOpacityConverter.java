package fr.pigeo.rimap.rimaprcp.swt.bindings;

import org.eclipse.core.databinding.conversion.Converter;

public class ScaleToOpacityConverter extends Converter {

	public ScaleToOpacityConverter() {
		super(int.class, double.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object convert(Object fromObject) {
		if (fromObject instanceof Integer) {
			int scale = (int) fromObject;
			double opacity = ((double) scale) / 100;
			//System.out.println("setting opacity to "+opacity);
			return opacity;
		}
		return null;
	}

}
