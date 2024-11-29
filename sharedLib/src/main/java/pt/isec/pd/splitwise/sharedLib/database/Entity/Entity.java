package pt.isec.pd.splitwise.sharedLib.database.Entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@SuperBuilder
public abstract class Entity implements Serializable {
	protected int id;

	public Entity(int id) {
		this.id = id;
	}

}
