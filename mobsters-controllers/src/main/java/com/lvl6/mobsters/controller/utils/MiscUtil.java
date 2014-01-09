package com.lvl6.mobsters.controller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	/*
	 * aMap (the second argument) might be modified
	 */
	public void retainValidMapEntries(Set<UUID> domain,  Map<UUID, ?> aMap,
			boolean keepThingsInDomain, boolean keepThingsNotInDomain) {
		Set<UUID> selectedIds = aMap.keySet();
		selectedIds = new HashSet<UUID>(selectedIds);

		for (UUID selectedId : selectedIds) {
			if (domain.contains(selectedId) && keepThingsInDomain) {
				continue;
			}
			if (!domain.contains(selectedId) && keepThingsNotInDomain) {
				continue;
			}
			//since selectedId isn't in the domain and want to keep things in domain
			//or is in the domain and want to keep things not in domain, remove it
			Object umhp = aMap.remove(selectedId);
			log.warn("Not retaining. object=" + umhp + "; keepThingsInDomain=" + keepThingsInDomain +
					"; keepThingsNotInDomain=" + keepThingsNotInDomain);
		}
	}

	/*
	 * ids (the second argument) might be modified
	 */
	public void retainValidListEntries(Set<UUID> existing, List<UUID> ids) {
		//	  	ids.add(123456789L);
		//	  	log.info("existing=" + existing + "\t ids=" + ids);

		List<UUID> copyIds = new ArrayList<UUID>(ids);
		// remove the invalid ids from ids client sent 
		// (modifying argument so calling function doesn't have to do it)
		ids.retainAll(existing);

		if (copyIds.size() != ids.size()) {
			//client asked for invalid ids
			log.warn("client asked for some invalid ids. asked for ids=" + copyIds + 
					"\t existingIds=" + existing + "\t remainingIds after purge =" + ids);
		}
	}

	//only the entries in the map that have their key in validIds will be kept  
	public Map<UUID, Integer> getValidMapEntries(List<UUID> validIds, 
			Map<UUID, Integer> idsToValues) {

		Map<UUID, Integer> returnMap = new HashMap<UUID, Integer>();

		for(UUID id : validIds) {
			int value = idsToValues.get(id);
			returnMap.put(id, value);
		}
		return returnMap;
	}
	
	public List<UUID> createUUIDListFromStrings(List<String> strings) {
		if (strings == null) return null;

		ArrayList<UUID> uuids = new ArrayList<UUID>();
		for (String string : strings) {
			uuids.add(UUID.fromString(string));
		}
		return uuids;
	}

	public String censorInput(String content, Set<String> blackList) {
		StringBuilder toReturn = new StringBuilder(content.length());

		String[] words = content.split(" ");
		String space = " "; //split by space, need to add them back in
		String w = "";

		for(int i = 0; i < words.length; i++) {
			w = words[i];

			//if at the last word, don't add a space after "censoring" it
			if ((words.length - 1) == i) {
				space = "";
			}
			//get rid of all punctuation
			String wWithNoPunctuation = w.replaceAll("\\p{Punct}", "");

			//the profanity table only holds lower case one word profanities
			if(blackList.contains(wWithNoPunctuation.toLowerCase())) {
				toReturn.append(asteriskify(w) + space);
			} else {
				toReturn.append(w + space);
			}
		}

		return toReturn.toString();
	}

	public String asteriskify(String wordToAskerify) {
		int len = wordToAskerify.length();
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < len; i++) {
			sb.append("*");
		}
		return sb.toString();
	}
	
	public <T> Collection<T> getValuesForIds (List<UUID> ids, Map<UUID, T> idsToValues) {
		Collection<T> values = new ArrayList<T>();
		
		for (UUID id : ids) {
			T value = idsToValues.get(id);
			values.add(value);
		}
		return values;
	}
	
	public int sumMapValues(Map<?, Integer> idsToValues) {
		int sum = 0;
		for (int value : idsToValues.values()) {
			sum += value;
		}
		return sum;
	}
}
