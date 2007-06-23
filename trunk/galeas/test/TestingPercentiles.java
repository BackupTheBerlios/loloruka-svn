package org.galeas.test;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class TestingPercentiles {

	public static void main(String args[]) {
		DescriptiveStatistics  stats = DescriptiveStatistics.newInstance(); 
		
/*		stats.addValue(2);
		stats.addValue(23);
		stats.addValue(28);
		stats.addValue(69);
		stats.addValue(87);
		stats.addValue(111);
		stats.addValue(125);*/
		
		stats.addValue(2);
		stats.addValue(4);
		stats.addValue(6);

		
		double p25 = stats.getPercentile(25);
		double p50 = stats.getPercentile(50);
		double p75 = stats.getPercentile(75);
		
		//System.out.println(stats.toString());
		
		double delta = p75 - p25;
		double center = p25 + (delta/2);
		double spread = delta/2;
		
		System.out.println("p25:"+p25+" - p50:"+p50+" - p75:"+p75);
		System.out.println("center: "+center+" - spread: "+spread);
	}
	
}
