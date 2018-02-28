package org.soshow.beautyedu.widget.treeview;

import org.soshow.beautyedu.bean.TreeNodeId;
import org.soshow.beautyedu.bean.TreeNodeLabel;
import org.soshow.beautyedu.bean.TreeNodePid;

public class TreeBean {
	@TreeNodeId
	private int _id;
	@TreeNodePid
	private int parentId;
	@TreeNodeLabel
	private String name;
	private long length;
	private String desc;

	public TreeBean(int _id, int parentId, String name) {
		super();
		this._id = _id;
		this.parentId = parentId;
		this.name = name;
	}

}
