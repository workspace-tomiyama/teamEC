package com.internousdev.latte.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.latte.dao.CartInfoDAO;
import com.internousdev.latte.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class CartAction extends ActionSupport implements SessionAware {
	private int totalPrice;
	private Map<String, Object> session;
	private List<CartInfoDTO> cartInfoDTO = new ArrayList<CartInfoDTO>();

	public String execute() {

		if(!session.containsKey("tempUserId") && !session.containsKey("userId")) {
			return "sessionTimeout";
		}

		CartInfoDAO dao = new CartInfoDAO();
		String userId;

		// いずれもここだけで使うローカルな変数
		// tempLogined: セッションから受け取るために一時的に保存する変数
		// cartLogined: ログインフラグが立っているかどうか判定するための変数
		String tempLogined = String.valueOf(session.get("logined")); //セッションから受け取ってString型に
		int cartLogined;
		// セッションから受け取った値がnullなのかそうでないのか
		// 結果をcartLoginedに代入
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

		cartInfoDTO = dao.getCartInfoDTO(userId);
		totalPrice = dao.getTotalPrice(userId);

		return SUCCESS;
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

}
