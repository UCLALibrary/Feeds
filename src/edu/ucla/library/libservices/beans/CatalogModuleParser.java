package edu.ucla.library.libservices.beans;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;

import edu.ucla.library.libservices.interfaces.rss.CatalogModule;

import org.jdom.Element;
import org.jdom.Namespace;

public class CatalogModuleParser implements ModuleParser
{
  private static final Namespace CATALOG_NS  = Namespace.getNamespace("catalog", CatalogModule.URI);
  public CatalogModuleParser()
  {
  }

  public String getNamespaceUri()
  {
    return CatalogModule.URI;
  }

  /**
   * @param root
   * @return
   */
  public Module parse( Element root )
  {
    boolean foundSomething;
    CatalogModule cm;

    foundSomething = false;
    cm = new CatalogModuleImpl();

    Element e = root.getChild("pubYear", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setPubYear(e.getText());
    }

    e = root.getChild("addedDate", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setAddedDate(e.getText());
    }

    e = root.getChild("bibId", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setBibId(e.getText());
    }

    e = root.getChild("subject", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setSubject(e.getText());
    }

    e = root.getChild("location", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setLocation(e.getText());
    }

    e = root.getChild("callNumber", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setCallNumber(e.getText());
    }

    e = root.getChild("author", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setAuthor(e.getText());
    }

    e = root.getChild("title", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setTitle(e.getText());
    }

    e = root.getChild("sortCallNumber", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setSortCallNumber(e.getText());
    }

    e = root.getChild("languageCode", CATALOG_NS);
    if (e != null) 
    {
        foundSomething = true;
        cm.setLanguageCode(e.getText());
    }

    return (foundSomething) ? cm : null;
  }
}