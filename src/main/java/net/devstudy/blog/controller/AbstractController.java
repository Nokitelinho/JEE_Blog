package net.devstudy.blog.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devstudy.blog.exception.ApplicationException;
import net.devstudy.blog.form.AbstractForm;
import net.devstudy.blog.service.BusinessService;
import net.devstudy.blog.service.impl.ServiceManager;

/**
 * 
 * @author devstudy
 * @see http://devstudy.net
 */
public abstract class AbstractController extends HttpServlet {
	private static final long serialVersionUID = 53415732680855255L;
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private BusinessService businessService;

	public final BusinessService getBusinessService() {
		return businessService;
	}

	public final int getOffset(HttpServletRequest req, int limit) {
		String val = req.getParameter("page");
		if (val != null) {
			int page = Integer.parseInt(val);
			return (page - 1) * limit;
		} else {
			return 0;
		}
	}

	@Override
	public final void init() throws ServletException {
		businessService = ServiceManager.getInstance(getServletContext()).getBusinessService();
	}

	public final void forwardToPage(String jspPage, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("currentPage", "page/" + jspPage);
		req.getRequestDispatcher("/WEB-INF/JSP/page-template.jsp").forward(req, resp);
	}

	public final void forwardToFragment(String jspPage, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/WEB-INF/JSP/fragment/" + jspPage).forward(req, resp);
	}

	public final <T extends AbstractForm> T createForm(HttpServletRequest req, Class<T> formClass) {
		try {
			T form = formClass.newInstance();
			form.setLocale(req.getLocale());
			BeanUtils.populate(form, req.getParameterMap());
			return form;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new ApplicationException("Can't create form "+formClass+" for request: "+e.getMessage(), e);
		}
	}
}
