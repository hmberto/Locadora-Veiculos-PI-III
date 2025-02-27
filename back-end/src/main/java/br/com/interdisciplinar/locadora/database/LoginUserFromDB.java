package br.com.interdisciplinar.locadora.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.interdisciplinar.locadora.dt.EnvVariables;
import br.com.interdisciplinar.locadora.mail.EmailConfirmation;

public class LoginUserFromDB {
	public static String NAME = LoginUserFromDB.class.getSimpleName();
	private static Logger LOG = Logger.getLogger(LoginUserFromDB.class.getName());
	
	public Map<Integer, String> LoginUser(String login, String pass, String newLogin, String loginInfo) {
		LOG.entering(NAME, "LoginUser");
		
		String sql = EnvVariables.getEnvVariable("DATABASE_GET_USER");
		String sql2 = EnvVariables.getEnvVariable("DATABASE_INSERT_USER_SESSION");
		
		Map<Integer, String> user = new HashMap<Integer, String>();
		Map<Integer, String> session = new HashMap<Integer, String>();
		
		String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String email = "";
		String nome = "";
		
		try {
			PreparedStatement statement = Database.connect().prepareStatement(sql);
			statement.setString(1, login);
			statement.setString(2, pass);
			
			ResultSet f = statement.executeQuery();
			
			while(f.next()) {
				for(int i = 1; i < 23; i++) {
					user.put(i, f.getString(i));
				}
				
				LOG.log(Level.INFO, "Data geted from the database. Login: " + login);
			}
			
			String userSession = "";
			if(user.get(2) == null || user.get(2).equals("null")) {}
			else {
				for(int i = 0; i < 50; i++) {
					int myindex = (int)(alphaNumeric.length() * Math.random());
					
					userSession = userSession + alphaNumeric.charAt(myindex);
				}
			}
			
			PreparedStatement statement2 = Database.connect().prepareStatement(sql2);
			statement2.setString(1, userSession);
			statement2.setString(2, login);
			
			statement2.execute();
			
			session.put(1, userSession);
						
			statement.close();
			
			email = user.get(6);
			nome = user.get(1);
			if(newLogin.equals("true")) {
				String[] nomeSeparado = nome.split(" ");
				
				String messageSubject = "Locadora de Veículos BH - Detectamos um novo acesso à sua conta";
				
				String messageText = "" 
						+ "<!DOCTYPE html>\n"
						+ "<html lang=\"pt-br\">\n"
						+ "<head>\n"
						+ "  <meta charset=\"UTF-8\">\n"
						+ "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
						+ "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
						+ "  <title>" + messageSubject + "</title>\n"
						+ "</head>\n"
						+ "<body>\n"
						+ "  <div role=\"banner\">\n"
						+ "    <div class=\"header\" style=\"Margin: 0 auto;max-width: 600px;min-width: 320px; width: 320px;width: calc(28000% - 167400px);\" id=\"emb-email-header-container\">\n"
						+ "      <div class=\"logo emb-logo-margin-box\" style=\"font-size: 26px;line-height: 32px;Margin-top: 16px;Margin-bottom: 32px;color: #41637e;font-family: sans-serif;Margin-left: 20px;Margin-right: 20px;\" align=\"center\">\n"
						+ "        <div class=\"logo-center\" align=\"center\" id=\"emb-email-header\"><img style=\"display: block;height: auto;width: 100%;border: 0;max-width: 141px;\" src=\"http://3.144.171.211/src/img/favicon.png\" alt width=\"141\"></div>\n"
						+ "      </div>\n"
						+ "    </div>\n"
						+ "  </div>\n"
						+ "  <div>\n"
						+ "    <div class=\"layout one-col fixed-width stack\" style=\"Margin: 0 auto;max-width: 600px;min-width: 320px; width: 320px;width: calc(28000% - 167400px);overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;\">\n"
						+ "    <div class=\"layout__inner\" style=\"border-collapse: collapse;display: table;width: 100%;background-color: #ffffff;\">\n"
						+ "    <div class=\"column\" style=\"text-align: left;color: #717a8a;font-size: 16px;line-height: 24px;font-family: sans-serif;\">\n"
						+ "    <div style=\"Margin-left: 20px;Margin-right: 20px;Margin-top: 24px;\">\n"
						+ "  </div>\n"
						+ "  <div style=\"Margin-left: 20px;Margin-right: 20px;\">\n"
						+ "    <h1 style=\"Margin-top: 0;Margin-bottom: 20px;font-style: normal;font-weight: normal;color: #3d3b3d;font-size: 30px;line-height: 38px;text-align: center;\">\n"
						+ "      " + nomeSeparado[0] + ", detectamos um novo acesso à sua conta" + "\n"
						+ "    </h1>\n"
						+ "  </div>\n"
						+ "  <div style=\"Margin-left: 20px;Margin-right: 20px;\">\n"
						+ "    <h2 class=\"size-24\" style=\"Margin-top: 0;Margin-bottom: 16px;font-style: normal;font-weight: normal;color: #3d3b3d;font-size: 20px;line-height: 28px;text-align: center;\" lang=\"x-size-24\">\n"
						+ "      " + loginInfo + "<br><br>\n"
						+ "    </h2>\n"
						+ "  </div>\n"
						+ "  <div style=\"Margin-left: 20px;Margin-right: 20px;\">\n"
						+ "    <div class=\"btn btn--flat btn--large\" style=\"Margin-bottom: 20px;text-align: center;\">\n"
						+ "      <a style=\"border-radius: 4px;display: inline-block;font-size: 14px;font-weight: bold;line-height: 24px;padding: 12px 24px;text-align: center;text-decoration: none !important;transition: opacity 0.1s ease-in;color: #ffffff !important;background-color: #337ab7;font-family: sans-serif;\" href=\"http://3.144.171.211/src/pages/logout.html?s=" + userSession + "\" target=\"_blank\">\n"
						+ "        Não fui eu\n"
						+ "      </a>\n"
						+ "  </div>\n"
						+ "</body>\n"
						+ "</html>";
				
				EmailConfirmation sendEmail = new EmailConfirmation();
				sendEmail.confirmation(email, messageSubject, messageText);
			}
		}
		catch (SQLException e) {
			LOG.log(Level.SEVERE, "Data not geted from the database: ", e);
		}
		finally {
			Database.disconnect();
		}
		
		LOG.exiting(NAME, "LoginUser");
		return session;
	}
}