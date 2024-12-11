package pt.isec.pd.splitwise.server_api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;

@RestController
@RequestMapping("groups")
@RequiredArgsConstructor
public class GroupController {
	private DataBaseManager dbManager;

	@GetMapping()
	public String getGroups() {
		return "Groups";
	}
}
