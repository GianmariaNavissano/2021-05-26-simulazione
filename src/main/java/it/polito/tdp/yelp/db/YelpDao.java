package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public Map<String, Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		Map<String, Business> result = new HashMap<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.put(business.getBusinessId(), business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getCities(){
		String sql = "SELECT distinct city "
				+ "FROM business";
		
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(res.getString("city"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Business> getVertex(Map<String, Business> idMap, String city, int year){
		String sql = "SELECT distinct b.business_id as id "
				+ "FROM business b, reviews r "
				+ "WHERE b.business_id = r.business_id "
				+ "AND b.city = ? AND YEAR(r.review_date)=?";
		
		List<Business> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getString("id"))) {
					result.add(idMap.get(res.getString("id")));
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getEdges(Map<String, Business> idMap, String city, int year){
		String sql = "SELECT b1.business_id as bus1, b2.business_id as bus2, AVG(r1.stars)-AVG(r2.stars) as peso "
				+ "FROM business b1, reviews r1, business b2, reviews r2 "
				+ "WHERE b1.business_id = r1.business_id AND b2.business_id = r2.business_id "
				+ "AND b1.city = ? AND b1.city = b2.city "
				+ "AND YEAR(r1.review_date)=? AND YEAR(r1.review_date) = YEAR(r2.review_date) "
				+ "AND b1.business_id<b2.business_id "
				+ "GROUP BY b1.business_id, b2.business_id "
				+ "HAVING AVG(r1.stars)-AVG(r2.stars)<>0";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getString("bus1")) && idMap.containsKey(res.getString("bus2"))) {
					Business b1 = idMap.get(res.getString("bus1"));
					Business b2 = idMap.get(res.getString("bus2"));
					double peso = res.getDouble("peso");
					
					if(peso>0)
						result.add(new Adiacenza(b2, b1, peso));
					else if(peso<0)
						result.add(new Adiacenza(b1, b2, (-1)*peso));
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
