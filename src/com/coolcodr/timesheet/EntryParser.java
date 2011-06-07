package com.coolcodr.timesheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class EntryParser {
    private static final Logger logger = Logger.getLogger(EntryParser.class);

    public void write(File f, List<Entry> entries) {
        try {
            Collections.sort(entries, Constants.getDatetimeComparator());
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");

            for (Entry entry : entries) {
                root.add(entry.toElement());
            }

            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(doc);
            writer.close();
        } catch (Exception ex) {
            logger.error(ex, ex);
        }
    }

    public List<Entry> load(File file) throws DocumentException, ParseException {
        Document doc = parse(file);
        List<Entry> rows = loadEntries(doc);
        Collections.sort(rows, Constants.getDatetimeComparator());
        return rows;
    }

    private List<Entry> loadEntries(Document doc) throws ParseException {
        ArrayList<Entry> list = new ArrayList<Entry>();
        List datarows = doc.selectNodes("//entry");
        Collections.reverse(datarows);
        for (Iterator i = datarows.iterator(); i.hasNext();) {
            Element datarow = (Element) i.next(); // do something
            Entry row = new Entry();
            row.readFrom(datarow);
            list.add(row);
        }
        return list;
    }

    private Document parse(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }
}
