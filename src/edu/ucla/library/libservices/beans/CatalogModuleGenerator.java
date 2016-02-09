package edu.ucla.library.libservices.beans;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleGenerator;

import edu.ucla.library.libservices.interfaces.rss.CatalogModule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;

public class CatalogModuleGenerator implements ModuleGenerator
{
  private static final Namespace CATALOG_NS = Namespace.getNamespace("catalog", CatalogModule.URI);
  private static final Set NAMESPACES;

  static 
  {
    Set nss = new HashSet();
    nss.add(CATALOG_NS);
    NAMESPACES = Collections.unmodifiableSet(nss);
  }

  public CatalogModuleGenerator()
  {
  }

  /**
   * @return
   */
  public String getNamespaceUri()
  {
    return CatalogModule.URI;
  }

  public Set getNamespaces()
  {
    return NAMESPACES;
  }

  public void generate( Module module, Element element )
  {
    Element root = element;
    CatalogModule cm = (CatalogModuleImpl)module;

    while (root.getParent()!=null && root.getParent() instanceof Element) 
    {
      root = (Element) element.getParent();
    }
    root.addNamespaceDeclaration(CATALOG_NS);

    if (cm.getAddedDate() != null) 
    {
      element.addContent(generateSimpleElement("addedDate", cm.getAddedDate()));
    }
    if (cm.getBibId() != null) 
    {
      element.addContent(generateSimpleElement("bibId", cm.getBibId()));
    }
    if (cm.getPubYear() != null) 
    {
      element.addContent(generateSimpleElement("pubYear", cm.getPubYear()));
    }
    if (cm.getSubject() != null) 
    {
      element.addContent(generateSimpleElement("subject", cm.getSubject()));
    }
    if (cm.getLocation() != null) 
    {
      element.addContent(generateSimpleElement("location", cm.getLocation()));
    }
    if (cm.getCallNumber() != null) 
    {
      element.addContent(generateSimpleElement("callNumber", cm.getCallNumber()));
    }
    if (cm.getAuthor() != null) 
    {
      element.addContent(generateSimpleElement("author", cm.getAuthor()));
    }
    if (cm.getTitle() != null) 
    {
      element.addContent(generateSimpleElement("title", cm.getTitle()));
    }
    if (cm.getSortCallNumber() != null) 
    {
      element.addContent(generateSimpleElement("sortCallNumber", cm.getSortCallNumber()));
    }
    if (cm.getLanguageCode() != null) 
    {
      element.addContent(generateSimpleElement("languageCode", cm.getLanguageCode()));
    }

  }
  protected Element generateSimpleElement(String name, String value)  
  {
    Element element = new Element(name, CATALOG_NS);
    element.addContent(value);
    return element;
  }
}
//