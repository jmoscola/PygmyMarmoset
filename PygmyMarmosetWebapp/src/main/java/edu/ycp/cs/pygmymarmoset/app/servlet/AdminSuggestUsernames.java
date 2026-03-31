// Pygmy Marmoset - an assignment submission webapp for CS courses
// Copyright (c) 2017, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This is free software distributed under the terms of the
// GNU Affero Public License v3 or later.  See LICENSE.txt for details.

package edu.ycp.cs.pygmymarmoset.app.servlet;

import java.io.Serial;

/**
 * Process username autocomplete suggestions for admin servlets.
 */
@Route(pattern="/a/suggestUsernames/*")
public class AdminSuggestUsernames extends AbstractSuggestUsernamesServlet {
	@Serial
	private static final long serialVersionUID = 1L;
}
