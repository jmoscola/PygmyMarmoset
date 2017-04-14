// Pygmy Marmoset - an assignment submission webapp for CS courses
// Copyright (c) 2017, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This is free software distributed under the terms of the
// GNU Affero Public License v3 or later.  See LICENSE.txt for details.

package edu.ycp.cs.pygmymarmoset.app.model;

public enum RoleType {
	STUDENT,
	INSTRUCTOR;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
	public boolean isInstructor() {
		return this == INSTRUCTOR;
	}
}
