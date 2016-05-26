package fr.pigeo.rimap.rimaprcp.core.ui.swt.bindings;

import org.eclipse.core.databinding.conversion.Converter;

public class OpacityToScaleConverter extends Converter {

	public OpacityToScaleConverter() {
		super(double.class, int.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object convert(Object fromObject) {
		if(fromObject instanceof Double){
				double opacity = (double) fromObject;
				int scale = (int) (opacity*100);
				//System.out.println("setting scale to "+scale);
		      return scale;
		    }
		return null;
	}

}
