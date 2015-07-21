package io.alatalab.glassbead;

import java.util.ResourceBundle;

import javax.annotation.Resource;

import boofcv.abst.geo.BundleAdjustmentCalibrated;

public class Props {
	static ResourceBundle bundle ;
	static{
		 bundle = ResourceBundle.getBundle("glassbead");
	}
	
	public static String getValue(String props){

	return bundle.getString(props);
	}

}
