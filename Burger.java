public class Burger {
	private int id;
	private String brand;
	private String name;
	private int price;
	
	
	public Burger() {}
	public Burger(int id, String brand, String name, int price) {
		this.id = id;
		this.brand = brand;
		this.name = name;
		this.price = price;
	} // 생성자
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	} // getter setter
	
	@Override
	public String toString() {
		return "Burger [id=" + id + ", brand=" + brand + ", name=" + name + ", price=" + price + "]";
	} // toString
	
	
}


//-------------------------------------------------------------------------------

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestBurgerDAO {
	private static BurgerDAO dao = new BurgerDAO();
	
	public static void testAdd() {
//		BurgerDAO dao = new BurgerDAO();
		Burger burger = new Burger();
		burger.setId(2);
		burger.setBrand("맘스터치");
		burger.setName("게살버거");
		burger.setPrice(3400);
		
		int result = dao.add(burger);
		System.out.println(result + "행 추가됨");
	}
	
	private static void testDelete(int id) {
		int result = dao.delete(id);
		System.out.println(result + "행 삭제됨");
	}
	private static void testUpdate(Burger burger) {
		int result = dao.update(burger);
		System.out.println(result + "행 수정됨");
	}
	
	private static void testSelectAtPrice(int min, int max) {
		List<Burger> list = dao.selectAtPrice(min, max);
		for (Burger b : list) {
			System.out.println(b);
		}
	}
	
	private static void testSelectAtBrand(String inputbrand) {
		List<Burger> list = dao.selectAtBrand(inputbrand);
		for (Burger b : list) {
			System.out.println(b);
		}
	}
	private static void testSelectAll() {
		List<Burger> list = dao.selectAll();
		for (Burger b : list) {
			System.out.println(b);
		}
	}
	
	private static void testAvgPriceByBrand() {
		Map<String, Integer> map = dao.selectAvgPriceByBrand();
		Iterator<String> iterator = map.keySet().iterator();
		
		while(iterator.hasNext()) {
			String key = iterator.next();
			Integer value = map.get(key);
			System.out.println(key + ":" + value);
			
//		for (Entry<String, Integer> entry : map.entrySet()) {
//			System.out.println(entry);
		}
	}
	public static void main(String[] args) {
//		testSelectAtBrand("맘스터치");
//		testSelectAtPrice();
//		testSelectAll();
//		testUpdate(new Burger(2, "맘스터치", "빅맥", 4800));
//		testDelete(9);
//		testAdd();
		testAvgPriceByBrand();
	}

}
// -------------------------------------------------------------------------------------------



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BurgerDAO {
	// 버거추가
	public int add(Burger burger) {
		String query = "INSERT INTO hamburger (id, brand, name, price) VALUES (?,?,?,?)";
		try(Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);){
			stmt.setInt(1, burger.getId());
			stmt.setString(2, burger.getBrand());
			stmt.setString(3, burger.getName());
			stmt.setInt(4, burger.getPrice());
			
			return stmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	// 버거전체조회
	public List<Burger> selectAll() {
		String query = "SELECT * FROM hamburger";
		List<Burger> list = new ArrayList<>();
		try (Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery();){
			while (rs.next()) {
				Burger b = new Burger();
				b.setId(rs.getInt("id"));
				b.setBrand(rs.getString("brand"));
				b.setName(rs.getString("name"));
				b.setPrice(rs.getInt("price"));
				list.add(b);
			} 
			}catch (SQLException e) {
				e.printStackTrace();
			}
		return list;
	}
	
	// 버거의 가격 범위로 조회
	public List<Burger> selectAtPrice(int min, int max){
		String query = "SELECT * FROM hamburger WHERE price BETWEEN ? AND ?";
		List<Burger> list = new ArrayList<>();
		ResultSet rs = null;
		try (Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				){
			stmt.setInt(1, min);
			stmt.setInt(2, max);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Burger b = new Burger();
				b.setId(rs.getInt("id"));
				b.setBrand(rs.getString("brand"));
				b.setName(rs.getString("name"));
				b.setPrice(rs.getInt("price"));
				list.add(b);
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	// 버거의 브랜드로 조회
	public List<Burger> selectAtBrand(String inputbrand){
		String query = "SELECT * FROM hamburger WHERE brand = ?";
		List<Burger> list = new ArrayList<>();
		ResultSet rs = null;
		try(Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				){
				
				stmt.setString(1, inputbrand);
				rs = stmt.executeQuery();
			while(rs.next()) {
				Burger b = new Burger();
				b.setId(rs.getInt("id"));
				b.setBrand(rs.getString("brand"));
				b.setName(rs.getString("name"));
				b.setPrice(rs.getInt("price"));
				list.add(b);
			}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return list;
		
	}
	// 버거 브랜드별 평균가격 //객체 하나 만들어보기
	public Map<String, Integer> selectAvgPriceByBrand() {
		Map<String, Integer> map = new LinkedHashMap<>();
		String query = "SELECT brand, AVG(price) AS avg FROM hamburger group by brand";
		try (Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery();){
			while (rs.next()) {
				String brand = rs.getString("brand");
				int avg = rs.getInt("avg");
				
				map.put(brand, avg);
			} 
			}catch (SQLException e) {
				e.printStackTrace();
			}
		return map;
	}
	
	// 버거수정
	public int update(Burger burger) {//id값과 field값만 변경해주면 
		String query = "UPDATE hamburger SET brand = ?, name = ?, price = ? WHERE id = ?";
		try (Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);){
			stmt.setString(1, burger.getBrand());
			stmt.setString(2, burger.getName());
			stmt.setInt(3, burger.getPrice());
			stmt.setInt(4, burger.getId());
			
			return stmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return 0;
	}
	// 버거삭제
	public int delete(int id) {
		String query = "DELETE FROM hamburger WHERE id = ?";
		try (Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);){
			stmt.setInt(1, id);
			
			return stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}

