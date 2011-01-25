/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-plartform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2011 geoSDI Group (CNR IMAA - Potenza - ITALY).
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. This program is distributed in the 
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. You should have received a copy of the GNU General 
 * Public License along with this program. If not, see http://www.gnu.org/licenses/ 
 *
 * ====================================================================
 *
 * Linking this library statically or dynamically with other modules is 
 * making a combined work based on this library. Thus, the terms and 
 * conditions of the GNU General Public License cover the whole combination. 
 * 
 * As a special exception, the copyright holders of this library give you permission 
 * to link this library with independent modules to produce an executable, regardless 
 * of the license terms of these independent modules, and to copy and distribute 
 * the resulting executable under terms of your choice, provided that you also meet, 
 * for each linked independent module, the terms and conditions of the license of 
 * that module. An independent module is a module which is not derived from or 
 * based on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obligated to do so. If you do not 
 * wish to do so, delete this exception statement from your version. 
 *
 */
package org.geosdi.geoplatform.gui.client.widget.scale;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.geosdi.geoplatform.gui.client.widget.map.store.Scale;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Francesco Izzi - CNR IMAA - geoSDI Group
 * 
 */
public class GPScaleWidget extends ContentPanel {

	private static Stack<GPScaleWidget> infoStack = new Stack<GPScaleWidget>();
	private static ArrayList<GPScaleWidget> slots = new ArrayList<GPScaleWidget>();
	protected GPScaleConfig config;
	protected int level;

	/**
	 * Creates a new GPScaleWidget instance.
	 */
	public GPScaleWidget() {
		baseStyle = "x-info";
		frame = true;
		setShadow(true);
		setLayoutOnChange(true);
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		if (GXT.isAriaEnabled()) {
			Accessibility.setRole(getElement(), "alert");
		}
	}

	public static void display(String title) {
		display(new GPScaleConfig(title));
	}

	public static void display(GPScaleConfig config) {
		pop().show(config);
	}

	private static GPScaleWidget pop() {
		GPScaleWidget info = infoStack.size() > 0 ? (GPScaleWidget) infoStack
				.pop() : null;
		if (info == null) {
			info = new GPScaleWidget();
		}
		return info;
	}

	/**
	 * Displays the ScaleBar.
	 * 
	 * @param config
	 *            the info config
	 */
	public void show(GPScaleConfig config) {
		this.config = config;
		onShowInfo();
	}

	private static void push(GPScaleWidget scale) {
		infoStack.push(scale);
	}

	protected void onShowInfo() {
		RootPanel.get().add(this);
		el().makePositionable(true);

		setTitle();
		setText();

		List<Scale> scales = new ArrayList<Scale>();
		
		scales.add(new Scale("1:1000"));
		scales.add(new Scale("1:10000"));
		scales.add(new Scale("1:100000"));
		scales.add(new Scale("1:1000000"));
		scales.add(new Scale("1:10000000"));
		scales.add(new Scale("1:100000000"));
		scales.add(new Scale("1:1000000000"));

		ListStore<Scale> scaleStore = new ListStore<Scale>();
		scaleStore.add(scales);
		
		ComboBox<Scale> comboScale = new ComboBox<Scale>();
		comboScale.setEmptyText("Select a scale...");
		comboScale.setDisplayField("scale");
		comboScale.setWidth(150);
		comboScale.setStore(scaleStore);
		comboScale.setTypeAhead(true);
		comboScale.setTriggerAction(TriggerAction.ALL);

		add(comboScale);

		level = firstAvail();
		slots.add(level, this);

		Point p = position();
		el().setLeftTop(p.x, p.y);
		setSize(config.width, config.height);

	}

	protected Point position() {
		Size s = XDOM.getViewportSize();
		int left = s.width - config.width - 10 + XDOM.getBodyScrollLeft();
		int top = s.height - config.height - 10
				- (level * (config.height + 10)) + XDOM.getBodyScrollTop();
		return new Point(left, top);
	}

	private static int firstAvail() {
		int size = slots.size();
		for (int i = 0; i < size; i++) {
			if (slots.get(i) == null) {
				return i;
			}
		}
		return size;
	}

	private void setTitle() {
		if (config.title != null) {
			head.setVisible(true);
			if (config.params != null) {
				config.title = Format.substitute(config.title, config.params);
			}
			setHeading(config.title);
		} else {
			head.setVisible(false);
		}
	}

	private void setText() {
		if (config.text != null) {
			if (config.params != null) {
				config.text = Format.substitute(config.text, config.params);
			}
			removeAll();
			addText(config.text);
		}
	}

}