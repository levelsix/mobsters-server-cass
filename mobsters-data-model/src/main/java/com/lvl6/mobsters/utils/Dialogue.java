package com.lvl6.mobsters.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dialogue implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(Dialogue.class);
	  
	private static final long serialVersionUID = -7818501595680769410L;
	private static String delimiter = "~";
	
	List<Integer> speakerInts = null;
	
	List<String> speakerTexts = null;


	
	
	public List<Integer> getSpeakerInts() {
		return speakerInts;
	}


	public void setSpeakerInts(List<Integer> speakerInts) {
		this.speakerInts = speakerInts;
	}


	public List<String> getSpeakerTexts() {
		return speakerTexts;
	}


	public void setSpeakerTexts(List<String> speakerTexts) {
		this.speakerTexts = speakerTexts;
	}

	public void setDialogue(String dialogue) {
		if (null == dialogue || dialogue.isEmpty()) {
			setSpeakerTexts(new ArrayList<String>());
			setSpeakerInts(new ArrayList<Integer>());
			return;
		}
		
		StringTokenizer st = new StringTokenizer(dialogue, delimiter);
		
		List<Integer> speakerIntList = new ArrayList<Integer>();
		List<String> speakerTextList = new ArrayList<String>();
		
		try {
			while (st.hasMoreTokens()) {
				Integer speaker = Integer.parseInt(st.nextToken());
				String speakerText = st.nextToken();
				
				if (null != speakerText && !speakerText.isEmpty()) {
					speakerIntList.add(speaker);
					speakerTextList.add(speakerText);
				}
			}
		} catch (Exception e) {
			log.error("problem with creating dialogue object for this dialogue: {}", dialogue, e);
		}
		
		setSpeakerInts(speakerIntList);
		setSpeakerTexts(speakerTextList);
	}

	@Override
	public String toString() {
		return "Dialogue [speakerInts=" + speakerInts + ", speakerTexts="
				+ speakerTexts + "]";
	}

}
