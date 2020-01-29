package com.internousdev.latte.action;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.latte.dao.CartInfoDAO;
import com.internousdev.latte.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteCartAction extends ActionSupport implements SessionAware {
	private int totalPrice;
	private List<CartInfoDTO> cartInfoDTO;
	private int[] checkList;
	private Map<String, Object> session;

	public String execute() throws SQLException {

		if(!session.containsKey("tempUserId") && !session.containsKey("userId")) {
			return "sessionTimeout";
		}

		String result = ERROR;
		int rs = 0;
		CartInfoDAO cartInfoDAO = new CartInfoDAO();

		// CartActionと同じ
		String userId;
		String tempLogined = String.valueOf(session.get("logined"));
		int cartLogined;
		if (tempLogined == null) {
			cartLogined = 0;
		} else {
			cartLogined = Integer.parseInt(tempLogined);
		}

		if (cartLogined == 1) {
			userId = session.get("userId").toString();
		} else {
			userId = session.get("tempUserId").toString();
		}

		for(int productId:checkList) {
			rs += cartInfoDAO.deleteCartInfo(productId, userId);
		}

		if (rs == checkList.length) {
			cartInfoDTO = cartInfoDAO.getCartInfoDTO(userId);
			totalPrice = cartInfoDAO.getTotalPrice(userId);
			result = SUCCESS;
		}

		return result;
	}

	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<CartInfoDTO> getCartInfoDTO() {
		return cartInfoDTO;
	}
	public void setCartInfoDTO(List<CartInfoDTO> cartInfoDTO) {
		this.cartInfoDTO = cartInfoDTO;
	}

	public Map<String, Object> getSession() {
		return session;
	}
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public int[] getCheckList() {
		return checkList;
	}
	public void setCheckList(int[] checkList) {
		this.checkList = checkList;
	}

}
