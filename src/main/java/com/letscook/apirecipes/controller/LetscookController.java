package com.letscook.apirecipes.controller;

import com.letscook.apirecipes.config.ConfigProperties;
import com.letscook.apirecipes.constantes.Constantes;
import com.letscook.apirecipes.dto.DataDTO;
import com.letscook.apirecipes.exceptions.BusinessException;
import com.letscook.apirecipes.service.LetscookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins="*")
@Api(value= Constantes.API_DESCRIPTION)
public class LetscookController {

    private final ConfigProperties configProperties;
    private final LetscookService letscookService;

    public LetscookController(ConfigProperties configProperties, LetscookService letscookService) {
        this.configProperties = configProperties;
        this.letscookService = letscookService;
    }

    @RequestMapping(value = Constantes.PATH_ENDPOINT, method = RequestMethod.GET)
    @ApiOperation(value=Constantes.ENDPOINT_DESCRIPTION)
    public ResponseEntity<List<DataDTO>> index(@Valid @RequestParam(required=true,name="i") String i) {
        List<DataDTO> lista = null;
        HttpStatus status = HttpStatus.OK;

        try {
            if (i != null) {
                if (i.isEmpty()) {
                    status = HttpStatus.BAD_REQUEST;
                    throw new BusinessException(configProperties.getERROR_INVALID_PARAMETER());
                } else {
                    String[] list = i.split(",");
                    if (list.length > 3) {
                        status = HttpStatus.NOT_ACCEPTABLE;
                        throw new BusinessException(configProperties.getERROR_MAX_EXCEED_RECIPES());
                    }
                }
            } else {
                status = HttpStatus.BAD_REQUEST;
                throw new BusinessException(configProperties.getERROR_INVALID_PARAMETER());
            }

            lista = letscookService.getRecipes(i);

            if (lista == null) {
                status = HttpStatus.NOT_FOUND;
                throw new BusinessException(configProperties.getERROR_RETURN_ENDPOINT());
            } else if (lista.isEmpty()) {
                status = HttpStatus.NOT_FOUND;
                throw new BusinessException(configProperties.getERROR_RETURN_ENDPOINT());
            }

        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e.getCause());
        }

        return new ResponseEntity<>(lista, status);
    }

}
