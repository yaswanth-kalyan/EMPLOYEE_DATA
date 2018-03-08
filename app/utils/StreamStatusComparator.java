package utils;

import java.util.Comparator;

import models.leave.AppliedLeaves;


public class StreamStatusComparator implements Comparator<AppliedLeaves>{

	@Override
	public int compare(final AppliedLeaves o1, final AppliedLeaves o2) {
		return o2.id.compareTo(o1.id);
	}




}
