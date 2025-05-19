package tn.esprit.spring.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Beneficiaire implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idBeneficiaire;
	private int cin;
	private String nom;
	private String prenom;
	private String profession;
	private float salaire;


	@OneToMany(mappedBy="beneficiaire", fetch=FetchType.EAGER)
	@JsonIgnore
	@ToString.Exclude
	private Set<Assurance> assurances = new HashSet<Assurance>();
	
}
