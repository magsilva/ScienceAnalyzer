package net.sf.sysrev.gui.zk;

import java.util.Date;
import java.util.List;

import net.sf.sysrev.db.DataSource;
import net.sf.sysrev.engines.pdf.PDFDocument;
import net.sf.sysrev.engines.pdf.PDFTextExtractor;
import net.sf.sysrev.util.persistence.PersistenceService;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

public class Controller extends GenericForwardComposer implements ListitemRenderer
{
	private static final long serialVersionUID = 2560535692993939331L;

	protected PersistenceService<Long, DataSource> dataSourceService;

	protected Textbox name;

	protected Textbox source;

	protected Textbox content;

	protected ListModelList listModelList;

	protected Listbox list;
	
	protected DataSource selectedDataSource;

	public void doAfterCompose(Component comp) throws Exception
	{
		super.doAfterCompose(comp);
		listModelList = new ListModelList();
		list.setModel(listModelList);
		list.setItemRenderer(this);

		list.addEventListener("onSelect", new EventListener()
		{
			public void onEvent(Event e) throws Exception
			{
				int index = list.getSelectedIndex();
				selectedDataSource = (DataSource) listModelList.get(index);
				name.setValue(selectedDataSource.getName());
				source.setValue(selectedDataSource.getSource());
				return;
			}
		});

		refresh();
	}

	public void render(Listitem listItem, Object data) throws Exception
	{
		DataSource source = (DataSource) data;
		new Listcell(source.getName()).setParent(listItem);
		new Listcell(source.getSource()).setParent(listItem);
		new Listcell(source.getContent() + "").setParent(listItem);
	}

	public void refresh()
	{
		List<DataSource> datasources = dataSourceService.findAll();
		listModelList.clear();
		listModelList.addAll(datasources);
	}

	public void onClick$add(Event e)
	{
		String name = this.name.getValue();
		String source = this.source.getValue();

		if (source != null) {
			DataSource ds = new DataSource();

			if (name != null) {
				ds.setName(name);
			} else {
				ds.setName(source);
			}

			try {
				PDFTextExtractor extractor = new PDFTextExtractor();
				PDFDocument document = new PDFDocument(source);
				extractor.setPDFDocument(document);
				ds.setContent(extractor.extractText());
			} catch (Exception exception) {
			}

			dataSourceService.save(ds);
			refresh();
		}
		return;
	}

	public void onClick$delete(Event e)
	{
		if (selectedDataSource != null) {
			dataSourceService.delete(selectedDataSource);
			refresh();
		}
	}
	
	public PersistenceService<Long, DataSource> getDataSourceService()
	{
		return dataSourceService;
	}

	public void setDataSourceService(PersistenceService<Long, DataSource> dataSourceService)
	{
		this.dataSourceService = dataSourceService;
	}

}
