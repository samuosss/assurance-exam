package tn.esprit.spring.services;

import java.util.*;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.entities.Assurance;
import tn.esprit.spring.entities.Beneficiaire;
import tn.esprit.spring.entities.Contrat;
import tn.esprit.spring.entities.TypeContrat;
import tn.esprit.spring.repositories.IAssuranceRepository;
import tn.esprit.spring.repositories.IBeneficiaireRepository;
import tn.esprit.spring.repositories.IContratRepository;

@Slf4j
@Service
public class BeneficiaireServicesImpl implements IBeneficiaireServices {

	@Autowired
	IBeneficiaireRepository benefRepository;

	@Autowired
	IAssuranceRepository assuranceRepository;

	@Autowired
	IContratRepository contratRepository;

	@Override
	public Beneficiaire ajouterBeneficaire(Beneficiaire bf) {
		return benefRepository.save(bf);
	}

	@Override
	public Contrat ajouterContrat(Contrat c) {
		return contratRepository.save(c);
	}

	@Transactional
	@Override
	public Assurance ajouterAssurance(Assurance a, int cinBf, String matricule) {

		Beneficiaire b = benefRepository.getByCin(cinBf);
		Contrat c = contratRepository.findByMatricule(matricule);

		a.setBeneficiaire(b);
		a.setContrat(c);

		return assuranceRepository.save(a);
	}


	// First solution (Java code)
	// To get elements from a Set, we use next() :
	@Override
	public Contrat getContratBf(int idBf) {
		Beneficiaire beneficiaire = benefRepository.findById(idBf).orElse(null);

		Contrat contrat = beneficiaire.getAssurances().iterator().next().getContrat();
		Date oldDate = contrat.getDateEffet();

		for (Iterator<Assurance> it = beneficiaire.getAssurances().iterator(); it.hasNext(); ) {
			Assurance a = it.next();
			if (oldDate.after(a.getContrat().getDateEffet())) {
				contrat = a.getContrat();
			}
		}

		return contrat;
	}


	@Override
	public float getMontantBf(int cinBf) {
		Beneficiaire beneficiaire = benefRepository.getByCin(cinBf);
		float montantContrat = 0;
		for (Assurance ass : beneficiaire.getAssurances()) {
			if (ass.getContrat().getType() == TypeContrat.Mensuel) {
				montantContrat += ass.getMontant() * 12;
			} else if (ass.getContrat().getType() == TypeContrat.Semestriel) {
				montantContrat += ass.getMontant() * 2;
			} else {
				montantContrat += ass.getMontant();
			}
		}
		/* Methode 1 : JPQL pour annuel seulement */
		// return
		// assuranceRepository.getMontantAnnuelByBf(beneficiaire.getIdBeneficiaire());

		return montantContrat;
	}


	@Override
	public Set<Beneficiaire> getBeneficairesAsC(TypeContrat tc) {
		return benefRepository.getByAssuranceType(tc);
	}

	@Override
	@Scheduled(cron = "*/60 * * * * *")
	public void statistiques() {

		TreeMap<Integer, Integer> myStat = new TreeMap<>(Collections.reverseOrder());

		for (Beneficiaire b : benefRepository.findAll()) {
			myStat.put(b.getAssurances().size(), b.getCin());
		}
		for (Entry<Integer, Integer> va : myStat.entrySet()) {
			log.info(va.getKey() + "|" + va.getValue());
		}
	}

}
