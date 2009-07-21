/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package org.polepos.reporters;

import java.io.*;


public class DefaultReporterFactory {

	public static Reporter[] defaultReporters() {
		return new Reporter[] { 
			new PlainTextReporter(defaultReporterOutputPath()), 
			new PDFReporter(defaultReporterOutputPath()),
			new CSVReporter(defaultReporterOutputPath()), 
			new HTMLReporter(subfolderPath(defaultReporterOutputPath(), "html")),
		};
	}
    
	public static String defaultReporterOutputPath() {
		return new File(System.getProperty("polepos.result.dir", "doc/results")).getAbsolutePath();
	}

	public static String subfolderPath(String root, String subfolder) {
		return new File(new File(root), subfolder).getAbsolutePath();
	}
}
