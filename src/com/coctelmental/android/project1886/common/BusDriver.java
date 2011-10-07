package com.coctelmental.android.project1886.common;

import com.coctelmental.android.project1886.common.util.JsonHandler;

public class BusDriver {

	private String dni;
	private String fullName;
	private String password;
	private String email;
	private String companyCIF;
	private String companyAuthCode;
	
	public BusDriver() {}

	public BusDriver(String dni, String fullName, String password,
			String email, String companyCIF, String companyAuthCode) {
		this.dni = dni;
		this.fullName = fullName;
		this.password = password;
		this.email = email;
		this.companyCIF = companyCIF;
		this.companyAuthCode = companyAuthCode;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCompanyCIF(String companyCIF) {
		this.companyCIF = companyCIF;
	}

	public void setCompanyAuthCode(String companyAuthCode) {
		this.companyAuthCode = companyAuthCode;
	}

	public String getDni() {
		return dni;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getCompanyCIF() {
		return companyCIF;
	}

	public String getCompanyAuthCode() {
		return companyAuthCode;
	}
	
	public String toString() {
		return String.format("dni: %s\n" +
							 "fullName: %s\n" +
							 "email: %s\n" +
							 "companyCIF: %s", dni, fullName, email, companyCIF);
	}

	public String toJson() {
		return JsonHandler.toJson(this);	
	}	
		
}
