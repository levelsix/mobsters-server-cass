package com.lvl6.mobsters.controller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.mobsters.po.staticdata.BoosterItem;


public class BoosterPackStuffUtils {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public List<BoosterItem> determineBoosterItemsUserReceives(int amountUserWantsToPurchase,
			Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems) {
		List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();

		Collection<BoosterItem> items = allBoosterItemIdsToBoosterItems.values();
		List<BoosterItem> itemsList = new ArrayList<BoosterItem>(items);
		float sumOfProbabilities = sumProbabilities(allBoosterItemIdsToBoosterItems.values());    

		//selecting items at random with replacement
		for(int purchaseN = 0; purchaseN < amountUserWantsToPurchase; purchaseN++) {
			BoosterItem bi = selectBoosterItem(itemsList, sumOfProbabilities);
			if (null == bi) {
				continue;
			}
			itemsUserReceives.add(bi);
		}

		return itemsUserReceives;
	}

	public float sumProbabilities(Collection<BoosterItem> boosterItems) {
		float sumOfProbabilities = 0.0f;
		for (BoosterItem bi : boosterItems) {
			sumOfProbabilities += bi.getChanceToAppear();
		}
		return sumOfProbabilities;
	}

	public BoosterItem selectBoosterItem(List<BoosterItem> itemsList,
			float sumOfProbabilities) {
		Random rand = new Random();
		float unnormalizedProbabilitySoFar = 0f;
		float randFloat = rand.nextFloat();

		log.info("selecting booster item. sumOfProbabilities=" + sumOfProbabilities +
				"\t randFloat=" + randFloat);

		int size = itemsList.size();
		//for each item, normalize before seeing if it is selected
		for(int i = 0; i < size; i++) {
			BoosterItem item = itemsList.get(i);

			//normalize probability
			unnormalizedProbabilitySoFar += item.getChanceToAppear();
			float normalizedProbabilitySoFar = unnormalizedProbabilitySoFar / sumOfProbabilities;

			log.info("boosterItem=" + item + "\t normalizedProbabilitySoFar=" +
					normalizedProbabilitySoFar);

			if(randFloat < normalizedProbabilitySoFar) {
				//we have a winner! current boosterItem is what the user gets
				return item;
			}
		}

		log.error("maybe no boosterItems exist. boosterItems=" + itemsList);
		return null;
	}

	public int determineGemReward(List<BoosterItem> boosterItems) {
		int gemReward = 0;
		for (BoosterItem bi : boosterItems) {
			gemReward += bi.getGemReward();
		}

		return gemReward;
	}
	
	public List<Integer> getIdsFromBoosterItems(List<BoosterItem> boosterItems) {
		List<Integer> ids = new ArrayList<Integer>();
		for (BoosterItem bi : boosterItems) {
			int biId = bi.getId();
			ids.add(biId);
		}
		return ids;
	}
}