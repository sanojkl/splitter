package de.hhu.propra.splitter.web.controller;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.exceptions.GruppeNotFoundException;
import de.hhu.propra.splitter.exceptions.PersonNotFoundException;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.forms.AusgabeHinzufuegenForm;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AusgabeHinzufuegenController {

  final GruppenService gruppenService;


  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public AusgabeHinzufuegenController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("/gruppe/ausgabeHinzufuegen")
  public String ausgabeHinzufuegenView(Model m,
      @ModelAttribute AusgabeHinzufuegenForm ausgabeHinzufuegenForm,
      OAuth2AuthenticationToken token, @RequestParam(value = "nr") long gruppeId) {
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), gruppeId);
    if (gruppe.isOffen()) {
      m.addAttribute("gruppeId", gruppeId);
      m.addAttribute("mitglieder", gruppe.getMitglieder());
      return "ausgabeHinzufuegen";
    }
    return "redirect:/gruppe?nr=" + gruppeId;
  }

  @PostMapping("/gruppe/ausgabeHinzufuegen")
  public String ausgabeHinzufuegen(Model m, OAuth2AuthenticationToken token,
      @Valid AusgabeHinzufuegenForm ausgabeHinzufuegenForm, BindingResult bindingResult,
      HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      if (ausgabeHinzufuegenForm.gruppeId() == null) {
        throw new GruppeNotFoundException();
      }
      Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
          token.getPrincipal().getAttribute("login"), ausgabeHinzufuegenForm.gruppeId());
      m.addAttribute("gruppeId", ausgabeHinzufuegenForm.gruppeId());
      m.addAttribute("mitglieder", gruppe.getMitglieder());
      return "ausgabeHinzufuegen";
    }
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), ausgabeHinzufuegenForm.gruppeId());
    if (!gruppe.isOffen()) {
      throw new GruppeNotFoundException();
    }

    try {
      gruppenService.addAusgabe(ausgabeHinzufuegenForm.gruppeId(),
          ausgabeHinzufuegenForm.beschreibung(),
          Money.parse("EUR " + ausgabeHinzufuegenForm.betrag()),
          ausgabeHinzufuegenForm.glaeubiger(), ausgabeHinzufuegenForm.schuldner());
    } catch (PersonNotFoundException e) {
      bindingResult.addError(new ObjectError("FormError", "Person nicht gefunden"));
      return "ausgabeHinzufuegen";
    }
    return "redirect:/gruppe?nr=" + ausgabeHinzufuegenForm.gruppeId();
  }
}
