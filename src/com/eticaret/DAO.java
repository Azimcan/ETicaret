package com.eticaret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.eticaret.Urun;
import com.eticaret.Kullanici;

public class DAO {
	
	private String jdbcURL;
	private String jdbcUsername;
	private String jdbcPassword;
	private Connection jdbcConnection;
	
	public DAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
		this.jdbcURL = jdbcURL;
		this.jdbcUsername = jdbcUsername;
		this.jdbcPassword = jdbcPassword;
	}
	
	protected void connect() throws SQLException {
		if (jdbcConnection == null || jdbcConnection.isClosed()) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			}
			catch (ClassNotFoundException e) {
				throw new SQLException(e);
			}
			jdbcConnection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		}
	}
	
	protected void disconnect() throws SQLException {
		if (jdbcConnection != null && !jdbcConnection.isClosed()) {
			jdbcConnection.close();
		}
	}
	
	public List<Urun> urunleriListele() throws SQLException {
		List<Urun> urunListesi = new ArrayList<>();
		
		String sql = "SELECT * FROM Urun";
		
		connect();
		
		Statement statement = jdbcConnection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		
		while (resultSet.next()) {
			int urun_id = resultSet.getInt("urun_id");
			String adi = resultSet.getString("adi");
			int stok = resultSet.getInt("stok");
			float fiyat = resultSet.getFloat("fiyat");
			int kategori_id = resultSet.getInt("kategori_id");
		
			Urun urun = new Urun(urun_id, adi, stok, fiyat, kategori_id);
			urunListesi.add(urun);
		}
		
		resultSet.close();
		statement.close();

		disconnect();

		return urunListesi;
	}
	
	public boolean kullaniciKayit(Kullanici kullanici) throws SQLException {
		String sql = "INSERT INTO kullanici(adi_soyadi, telefon, eposta, adres, sifre) VALUES(?, ?, ?, ?, ?)";
		connect();

		PreparedStatement statement = jdbcConnection.prepareStatement(sql);
		statement.setString(1, kullanici.getAdi_soyadi());
		statement.setString(2, kullanici.getTelefon());	
		statement.setString(3, kullanici.getEmail());	
		statement.setString(4, kullanici.getAdres());	
		statement.setString(5, kullanici.getSifre());

		boolean rowInserted = statement.executeUpdate() > 0;
		statement.close();
		disconnect();
		return rowInserted;
	}
	
	public String kullaniciGiris(Kullanici kullanici) throws SQLException {
		
		String sql = "SELECT * FROM kullanici WHERE eposta= ? and sifre = ?";
		connect();
		PreparedStatement statement = jdbcConnection.prepareStatement(sql);
		statement.setString(1, kullanici.getEmail());
		statement.setString(2, kullanici.getSifre());
		ResultSet rs = statement.executeQuery();
		String e_posta = null;
		while(rs.next())
		 {
			e_posta = rs.getString("eposta"); 
		 }
		rs.close();
		statement.close();
		return e_posta;
		
	}
	
	public Kullanici kullaniciBilgileri(String e_posta) throws SQLException {
		Kullanici kullanici= null;
		String sql = "SELECT * FROM kullanici WHERE eposta = ?";
		
		connect();
		
		PreparedStatement statement = jdbcConnection.prepareStatement(sql);
		statement.setString(1, e_posta);
		
		ResultSet resultSet = statement.executeQuery();
		
		if (resultSet.next()) {
			String adi_soyadi = resultSet.getString("adi_soyadi");
			String telefon = resultSet.getString("telefon");
			String eposta = resultSet.getString("eposta");
			String adres = resultSet.getString("adres");
			
			kullanici = new Kullanici(adi_soyadi, telefon, eposta, adres);
		}
		
		resultSet.close();
		statement.close();
		
		return kullanici;
	}
	
	public boolean kullaniciBilgileriDuzenle(Kullanici kullanici) throws SQLException {
		String sql = "UPDATE kullanici SET adi_soyadi = ?, telefon = ?, adres = ?, sifre = ?";
		sql += " WHERE eposta = ?";
		connect();
		
		PreparedStatement statement = jdbcConnection.prepareStatement(sql);
		statement.setString(1, kullanici.getAdi_soyadi());
		statement.setString(2, kullanici.getTelefon());
		statement.setString(3, kullanici.getAdres());
		statement.setString(4, kullanici.getSifre());
		statement.setString(5, kullanici.getEmail());
		
		boolean rowUpdated = statement.executeUpdate() > 0;
		statement.close();
		disconnect();
		return rowUpdated;		
	}
	
	public List<Siparis> kullaniciSiparisleri(String e_posta) throws SQLException {
		List<Siparis> siparisler = new ArrayList<>();
		
		String sql = "SELECT * FROM Siparis WHERE eposta=?";
		 
		connect();
		
		PreparedStatement statement = jdbcConnection.prepareStatement(sql);
		statement.setString(1, e_posta);
		ResultSet resultSet = statement.executeQuery();
		
		while (resultSet.next()) {
			int siparis_id = resultSet.getInt("siparis_id");
			String eposta = resultSet.getString("eposta");
			String tarih = resultSet.getString("tarih");
			String odeme_sekli = resultSet.getString("odeme_sekli");
	
		
			Siparis siparis = new Siparis(siparis_id, eposta, tarih, odeme_sekli);
			siparisler.add(siparis);
		}
		
		resultSet.close();
		statement.close();

		disconnect();

		return siparisler;
	}
	
	public List<SiparisDetay> kullaniciSiparisleriDetay(int siparis_id_gelen) throws SQLException {
		List<SiparisDetay> siparisDetayListesi = new ArrayList<>();
		String sql = "SELECT * FROM SiparisDetay";
		sql += " WHERE siparis_id = ?";
		connect();
		PreparedStatement statement = jdbcConnection.prepareStatement(sql);
		statement.setInt(1, siparis_id_gelen);
		ResultSet resultSet = statement.executeQuery();
		 
		while (resultSet.next()) {
			int siparis_id = resultSet.getInt("siparis_id");
			int urun_id = resultSet.getInt("urun_id");
			int fiyat = resultSet.getInt("fiyat");
			int adet = resultSet.getInt("adet");
		
			SiparisDetay siparisDetay = new SiparisDetay(siparis_id, urun_id, fiyat, adet);
			siparisDetayListesi.add(siparisDetay);
		}
		
		resultSet.close();
		statement.close();
		 
		disconnect();
		 
		return siparisDetayListesi;
	}
	
	public void kullaniciSiparisSil(int siparis_id) throws SQLException {
		connect();
		
		String sql = "DELETE FROM Siparis WHERE siparis_id=?";
		PreparedStatement statement = jdbcConnection.prepareStatement(sql);
		statement.setInt(1, siparis_id);
		statement.executeUpdate();
		statement.close();
		
		String sql2 = "DELETE FROM SiparisDetay WHERE siparis_id=?";
		PreparedStatement statement2 = jdbcConnection.prepareStatement(sql2);
		statement2.setInt(1, siparis_id);
		statement2.executeUpdate();
		statement2.close();
		
		disconnect();
	}
	

}