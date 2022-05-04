package br.com.alura.forum.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.controller.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;


@RestController// substitui o responsabudy
@RequestMapping("/topicos")
public class TopicosController {
@Autowired
private TopicoRepository topicoRepository;
	
@Autowired
private CursoRepository cursoRepository;
	
	@GetMapping
	public List <TopicoDto> lista(String nomeCurso){
		if(nomeCurso == null) {
		List<Topico> topicos = topicoRepository.findAll();
		return TopicoDto.converter(topicos);
	} else {
		List<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso);// quando tiver nomes parecidos entre atribudos e relcionamentos poderemos utilizar _
		return TopicoDto.converter(topicos);
	}

}
	@PostMapping
	@Transactional
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);
		URI uri =  uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();// não temos esses tópicos detalhado, mas vamos fazer ao poucos .
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
	@GetMapping("/{id}")// aqui eu preciso detalhar para não ter conflito
	public ResponseEntity<DetalhesTopicoDto> detalhar(@PathVariable Long id) {
		Optional<Topico> topico = topicoRepository.findById(id);// ELE CONSIDERA QUE TENHA UM REGISTO, CASO NÃOO TENHA ELE DEVOLVE NULO/ EXCEPTION, or isso eu substituo getOne porFindById
		if(topico.isPresent()) {
			return ResponseEntity.ok( new DetalhesTopicoDto(topico.get()));
		}
		
		return ResponseEntity.notFound().build(); 
	}
	
	@PutMapping("/id")// metódo que faz toda atualização
	@Transactional
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form ){
		
		
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
			}
		
		return ResponseEntity.notFound().build(); 
	}
		
		
	
	@DeleteMapping("/id")
	@Transactional
	public ResponseEntity<TopicoDto> remover(@PathVariable Long id){
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
		topicoRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}
		return ResponseEntity.notFound().build();
	
	}
}


