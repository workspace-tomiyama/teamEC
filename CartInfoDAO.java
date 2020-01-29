package com.internousdev.latte.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.internousdev.latte.dto.CartInfoDTO;
import com.internousdev.latte.util.DBConnector;

public class CartInfoDAO {

	/** カート情報の取得
	 * @param: userId
	 * @return: cartInfoDTO */
	public List<CartInfoDTO> getCartInfoDTO(String userId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		List<CartInfoDTO> cartInfoDTO = new ArrayList<CartInfoDTO>();

		// テーブル結合：カート情報を軸にして商品情報を内部結合
		String sql = "SELECT ci.user_id, ci.product_id, pi.product_name, pi.product_name_kana,"
		+ " pi.price, pi.image_file_name, pi.image_file_path, pi.release_date, pi.release_company, ci.product_count,"
		+ " (ci.product_count * pi.price) as sub_total,"
		+ " ci.regist_date, ci.update_date"
		+ " FROM cart_info ci"
		+ " INNER JOIN product_info pi"
		+ " ON ci.product_id = pi.product_id"
		+ " WHERE ci.user_id = ?"
		+ " ORDER BY ci.update_date DESC, ci.regist_date DESC";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				CartInfoDTO dto = new CartInfoDTO();
				dto.setUserId(rs.getString("user_id"));
				dto.setProductId(rs.getInt("product_id"));
				dto.setProductName(rs.getString("product_name"));
				dto.setProductNameKana(rs.getString("product_name_kana"));
				dto.setPrice(rs.getInt("price"));
				dto.setImageFileName(rs.getString("image_file_name"));
				dto.setImageFilePath(rs.getString("image_file_path"));
				dto.setReleaseDate(rs.getDate("release_date"));
				dto.setReleaseCompany(rs.getString("release_company"));
				dto.setProductCount(rs.getInt("product_count"));
				dto.setSubtotal(rs.getInt("sub_total"));
				dto.setRegistDate(rs.getDate("regist_date"));
				dto.setUpdatedDate(rs.getDate("update_date"));
				cartInfoDTO.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return cartInfoDTO;
	}

	/** ユーザーIDと紐づくカート情報の存在判定（仮ユーザー本ユーザーの別を問わず使用可能）
	 * @param: userId
	 * @return: result（存在するかどうか） */
	public boolean isExistsCartInfo(String userId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "SELECT COUNT(*) AS COUNT FROM cart_info WHERE user_id = ?";
		boolean result = false;

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				if(rs.getInt("COUNT") > 0) {
					result = true;
				}

			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/** カート情報の存在判定
	 * @param: userId
	 * @param: productId
	 * @return: result（存在するかどうか） */
	public boolean isExistsProduct(String userId, int productId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "SELECT COUNT(*) AS COUNT FROM cart_info WHERE user_id = ? AND product_id = ?";
		boolean result = false;
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				if(rs.getInt("COUNT") > 0) {
					result = true;
				}

			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/** カート内合計金額取得
	 * @param: userId
	 * @return: totalPrice */
	public int getTotalPrice(String userId) {
		int totalPrice = 0;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "SELECT sum(product_count * price) as total_price "
				+ "FROM cart_info ci "
				+ "JOIN product_info pi "
				+ "ON ci.product_id = pi.product_id "
				+ "WHERE user_id = ?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				totalPrice = rs.getInt("total_price");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return totalPrice;
	}


	/** 仮ユーザーIDとユーザーIDの紐づけ
	 * @param: userId
	 * @param: tempUserId
	 * @return: rs（更新件数） */
	public int linkToUserId(String userId, String tempUserId, int productId) {
		//カート情報のテーブルのユーザーIDを、入力されたユーザーIDに更新する
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		// tempUserIdと一致するユーザーのカート情報のuser_idを、入力されたuserIdに更新
		String sql = "UPDATE cart_info SET user_id = ?, update_date = now() WHERE user_id = ? AND product_id = ?";
		int rs = 0;

			try {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, userId);
				ps.setString(2, tempUserId);
				ps.setInt(3, productId);
				rs = ps.executeUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		return rs;
	}

	/** 仮ユーザーの削除
	 * @param: userId（tempUserIdに限る）
	 * @return: rs（削除件数） */
	public int deleteTempUser(String userId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "DELETE FROM cart_info WHERE user_id = ?";
		int rs = 0;

			try {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, userId);
				rs = ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		return rs;
	}

	/** カート情報の更新（追加含む）
	 * @param: userId
	 * @param: productId
	 * @param: productCount
	 * @return: rs（更新件数） */
	public int updateCartInfo(String userId, int productId, int productCount) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int rs = 0;
		String sql = "UPDATE cart_info SET product_count = (product_count + ?), update_date = now() WHERE user_id = ? AND product_id = ?";

			try {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, productCount);
				ps.setString(2, userId);
				ps.setInt(3, productId);
				rs = ps.executeUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			return rs;
	}

	/** カート情報に登録
	 * @param: userId
	 * @param: productId
	 * @param: productCount
	 * @return: count（登録件数） */
	public int registCartInfo(String userId, int productId, int productCount) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		int count = 0;
		String sql = "INSERT INTO cart_info (user_id, product_id, product_count, regist_date, update_date) VALUES(?, ? ,?, now(), now())";

			try {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, userId);
				ps.setInt(2, productId);
				ps.setInt(3, productCount);
				count = ps.executeUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return count;
	}

	/** カート情報の削除
	 * @param: productId
	 * @param: userId
	 * @return: rs（削除件数） */
	public int deleteCartInfo(int productId, String userId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "DELETE FROM cart_info WHERE product_id = ? AND user_id = ?";
		int rs = 0;

			try {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, productId);
				ps.setString(2, userId);
				rs = ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		return rs;
	}

	/** カート情報の削除（購入履歴に登録した後カートの中身を空にする処理）
	 * @param: userId
	 * @return: rs（削除件数） */
	public int deleteAll(String userId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "DELETE FROM cart_info WHERE user_id = ?";
		int rs = 0;

			try {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, userId);
				rs = ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		return rs;
	}
}
