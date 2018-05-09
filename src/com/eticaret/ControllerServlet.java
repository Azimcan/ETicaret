package com.eticaret;
 
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
 
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/**
 * ControllerServlet.java
 * This servlet acts as a page controller for the application, handling all
 * requests from the user.
 * @author www.codejava.net
 */
public class ControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DAO dao;
 
    public void init() {
        String jdbcURL = getServletContext().getInitParameter("jdbcURL");
        String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
        String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
 
        dao = new DAO(jdbcURL, jdbcUsername, jdbcPassword);
 
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
 
        try {
            switch (action) {
        	// ================= KULLANICI =================
            case "/kullaniciGirisForm":
            	kullaniciGirisForm(request, response);
            	break;
            case "/kullaniciGiris":
            	kullaniciGiris(request, response);
            	break;
            case "/kullaniciKayitForm":
            	kayitForm(request, response);
            	break;
            case "/kullaniciKayit":
            	kullaniciKayit(request, response);
            	break;
            case "/kullaniciBilgileriForm":
            	kullaniciBilgileriForm(request, response);
            	break;
            case "/kullaniciBilgileriDuzenle":
            	kullaniciBilgileriDuzenle(request, response);
            	break;
            case "/kullaniciSiparisleriForm":
            	kullaniciSiparisleriForm(request, response);
            	break;
            case "/kullaniciSiparisleriDetayForm":
            	kullaniciSiparisleriDetayForm(request, response);
            	break;
            case "/kullaniciSiparisSil":
            	kullaniciSiparisSil(request, response);
            	break;
            case "/sepeteEkle":
            	sepeteEkle(request, response);
            	break;
            case "/sepet":
            	sepet(request, response);
            	break;
            default:
                UrunListe(request, response);
                break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        
        }
    }
    
    private void UrunListe(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
		List<Urun> urunListesi = dao.urunleriListele();
		request.setAttribute("urunListesi", urunListesi);
		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
		dispatcher.forward(request, response);
	}
    
    // ================= KULLANICI =================
    private void kullaniciGirisForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("KullaniciGiris.jsp");
        dispatcher.forward(request, response);
    }
    
	private void kullaniciGiris(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
		String e_posta = request.getParameter("eposta");
		String sifre = request.getParameter("sifre");
		Kullanici kullanici = new Kullanici(e_posta, sifre);
		String e_posta_gelen= dao.kullaniciGiris(kullanici);
		String gidilecekSayfa = null;
		if(e_posta_gelen!=null)
			gidilecekSayfa = "KullaniciGirisHandler.jsp";
		else
			gidilecekSayfa = "KullaniciGiris.jsp?status=false";
		RequestDispatcher dispatcher = request.getRequestDispatcher(gidilecekSayfa);
    	dispatcher.forward(request, response);
	}
    
    private void kayitForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("KullaniciKayit.jsp");
        dispatcher.forward(request, response);
    }
    
    private void kullaniciKayit(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		String adi_soyadi = request.getParameter("name");
		String telefon = request.getParameter("telefon");
		String eposta = request.getParameter("eposta");
		String adres = request.getParameter("adres");
		String sifre = request.getParameter("password");


		Kullanici newKullanici = new Kullanici(adi_soyadi, telefon, eposta, adres, sifre);
		dao.kullaniciKayit(newKullanici);
		response.sendRedirect("list");
	}
    
    private void kullaniciBilgileriForm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
		String eposta = request.getParameter("eposta");
		Kullanici kullanici = dao.kullaniciBilgileri(eposta);
		RequestDispatcher dispatcher = request.getRequestDispatcher("KullaniciBilgileri.jsp");
		request.setAttribute("kullanici", kullanici);
		dispatcher.forward(request, response);
    }
    
    private void kullaniciBilgileriDuzenle(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		String adi_soyadi = request.getParameter("name");
		String telefon = request.getParameter("telefon");
		String eposta = request.getParameter("eposta");
		String adres = request.getParameter("adres");
		String sifre = request.getParameter("password");
		
		Kullanici kullanici = new Kullanici(adi_soyadi, telefon, eposta, adres, sifre);
		dao.kullaniciBilgileriDuzenle(kullanici);
		response.sendRedirect("list");
	}
	
    private void kullaniciSiparisleriForm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
		String eposta = request.getParameter("eposta");
		List<Siparis> siparisler = dao.kullaniciSiparisleri(eposta);
		RequestDispatcher dispatcher = request.getRequestDispatcher("KullaniciSiparisleri.jsp");
		request.setAttribute("siparisler", siparisler);
		dispatcher.forward(request, response);
    }
    
    private void kullaniciSiparisleriDetayForm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
		int siparis_id = Integer.parseInt(request.getParameter("siparis_id"));
		List<SiparisDetay> siparisDetaylari= dao.kullaniciSiparisleriDetay(siparis_id);
		RequestDispatcher dispatcher = request.getRequestDispatcher("KullaniciSiparisleriDetay.jsp");
		request.setAttribute("siparisDetaylari", siparisDetaylari);
		dispatcher.forward(request, response);
    }
    
    private void kullaniciSiparisSil(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		int siparis_id = Integer.parseInt(request.getParameter("siparis_id"));
		dao.kullaniciSiparisSil(siparis_id);
		response.sendRedirect("list");
	}
    
    private void sepeteEkle(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		String urun_id = request.getParameter("urun_id"); 
		Cookie c1 = new Cookie("asd", "qwe");
		response.addCookie(c1);  
		response.sendRedirect("list");
	}
    
    private void sepet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
    	Cookie cookies[] = request.getCookies();
    	List<Integer> urunler = new ArrayList<Integer>();
    	for (Cookie cookie : cookies ) {
    		  urunler.add(Integer.parseInt(cookie.getValue()));
    		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("Sepet.jsp");
		request.setAttribute("urunler", urunler);
		dispatcher.forward(request, response);
    }
    
    
    /*private void kullaniciSiparis(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		String urun = request.getParameter("name");
		String telefon = request.getParameter("telefon");
		String eposta = request.getParameter("eposta");
		int adres = request.getParameter("adres");
		String sifre = request.getParameter("password");


		Kullanici newKullanici = new Kullanici(adi_soyadi, telefon, eposta, adres, sifre);
		dao.kullaniciKayit(newKullanici);
		response.sendRedirect("list");
	}
    */
}