package com.wouterwillems.watchdog.tools.readers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.wouterwillems.watchdog.tools.models.ActivityType;
import com.wouterwillems.watchdog.tools.models.DayActivity;

public class WatchDogXmlReader {

	@SuppressWarnings("deprecation")
	public void parse(String path) throws SAXException, IOException,
			ParserConfigurationException {
		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();

		NodeList intervals = doc.getElementsByTagName("interval");

		Map<Long, DayActivity> actitiviesTest = new HashMap<Long, DayActivity>();
		Map<Long, DayActivity> actitiviesProd = new HashMap<Long, DayActivity>();

		for (int i = 0; i < intervals.getLength(); i++) {
			Node n = intervals.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) n;

				String type = getValue(eElement, "documentType");
				String start = getValue(eElement, "start");
				String end = getValue(eElement, "end");

				ActivityType acType = null;
				if (type.equals("TEST")) {
					acType = ActivityType.TEST;
				} else if (type.equals("PRODUCTION")) {
					acType = ActivityType.PRODUCTION;
				}

				if (acType != null) {
					Long duration = Long.parseLong(end) - Long.parseLong(start);

					Date date = new Date(Long.parseLong(start));
					Date date2 = new Date(date.getYear(), date.getMonth(),
							date.getDate(), date.getHours(), 0);

					if (acType == ActivityType.TEST) {
						if (!actitiviesTest.containsKey(date2.getTime())) {
							DayActivity dayActivity = new DayActivity();
							dayActivity.setDurationMilliseconds(duration);
							dayActivity.setDate(date2);
							dayActivity.setType(acType);

							actitiviesTest.put(date2.getTime(), dayActivity);
						} else {
							DayActivity dayActivity = actitiviesTest.get(date2
									.getTime());
							dayActivity.setDurationMilliseconds(dayActivity
									.getDurationMilliseconds() + duration);
						}
					} else if (acType == ActivityType.PRODUCTION) {
						if (!actitiviesProd.containsKey(date2.getTime())) {
							DayActivity dayActivity = new DayActivity();
							dayActivity.setDurationMilliseconds(duration);
							dayActivity.setDate(date2);
							dayActivity.setType(acType);

							actitiviesProd.put(date2.getTime(), dayActivity);
						} else {
							DayActivity dayActivity = actitiviesProd.get(date2
									.getTime());
							dayActivity.setDurationMilliseconds(dayActivity
									.getDurationMilliseconds() + duration);
						}

					}
				}
			}

		}
		PrintWriter writer = new PrintWriter("c:/kers.csv", "UTF-8");

		writeToFile(actitiviesTest, actitiviesProd, writer);

		writer.close();

	}

	private void writeToFile(Map<Long, DayActivity> activitiesTest, Map<Long, DayActivity> activitiesProd,
			PrintWriter writer) {
		SortedSet<Long> keysTest = new TreeSet<Long>(activitiesTest.keySet());
		SortedSet<Long> keysProd = new TreeSet<Long>(activitiesProd.keySet());
		int counter = 0;
		Date firstDay = activitiesTest.get(keysTest.first()).getDate();
		if(activitiesProd.get(keysProd.first()).getDate().before(firstDay)){
			firstDay = activitiesProd.get(keysProd.first()).getDate();
		}
		Date lastDay = activitiesTest.get(keysTest.last()).getDate();
		if(activitiesProd.get(keysProd.last()).getDate().after(lastDay)){
			lastDay = activitiesProd.get(keysProd.last()).getDate();
		}
		
		DateTime current = new DateTime(firstDay.getTime());
		DateTime last = new DateTime(lastDay.getTime());

		while (current.isBefore(last.getMillis() + 1)) {
			System.out.println((counter+1)+"/"+keysTest.size());
			DayActivity dayActivityTest = activitiesTest.get(current.getMillis());
			DayActivity dayActivityProd = activitiesProd.get(current.getMillis());

			if (dayActivityTest != null) {
				writer.print(current.toString() + ",");
				writer.print(dayActivityTest.getDurationMilliseconds()+",");
				counter++;
			} else {
				writer.print(current.toString()+",0,");
			}
			if (dayActivityProd != null) {
				writer.print(current.toString() + ",");
				writer.print(dayActivityProd.getDurationMilliseconds()+",");
				counter++;
			} else {
				writer.print(current.toString()+",0,");
			}
			writer.println("");
			current = current.plusHours(1);
		}

	}

	private String getValue(Element eElement, String name) {
		NodeList startNodeList = eElement.getElementsByTagName(name);
		Node startNode = startNodeList.item(0);
		String start = startNode.getTextContent();
		return start;
	}

	public static void main(String args[]) throws SAXException, IOException,
			ParserConfigurationException {
		WatchDogXmlReader r = new WatchDogXmlReader();
		r.parse(args[0]);
	}
}