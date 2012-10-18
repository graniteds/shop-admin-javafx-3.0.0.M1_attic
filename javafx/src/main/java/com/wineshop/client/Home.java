/*
  GRANITE DATA SERVICES
  Copyright (C) 2011 GRANITE DATA SERVICES S.A.S.

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or (at your
  option) any later version.

  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
  for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.
*/

package com.wineshop.client;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javax.inject.Inject;

import org.granite.client.tide.collections.javafx.PagedQuery;
import org.granite.client.tide.collections.javafx.TableViewSort;
import org.granite.client.tide.server.SimpleTideResponder;
import org.granite.client.tide.server.TideFaultEvent;
import org.granite.client.tide.server.TideResultEvent;
import org.springframework.stereotype.Component;

import com.wineshop.client.entities.Address;
import com.wineshop.client.entities.Vineyard;
import com.wineshop.client.services.VineyardRepository;


/**
 * 
 * @author william
 */
@Component
public class Home implements Initializable {

	@FXML
	private TextField fieldSearch;

	@FXML
	private TableView<Vineyard> tableVineyards;
	
	@FXML
	private Label labelFormVineyard;
	
	@FXML
	private TextField fieldName;
	
	@FXML
	private TextField fieldAddress;

	@FXML
	private Button buttonDelete;

	@FXML
	private Button buttonCancel;

	@Inject
	private PagedQuery<Vineyard, Vineyard> vineyards;

	@FXML
	private Vineyard vineyard;
	
	@Inject
	private VineyardRepository vineyardRepository;
	
	
	@SuppressWarnings("unused")
	@FXML
	private void search(ActionEvent event) {
		vineyards.refresh();
	}
	
	
	private void select(Vineyard vineyard) {
		if (vineyard == this.vineyard && this.vineyard != null)
			return;
		
		if (this.vineyard != null) {
			fieldName.textProperty().unbindBidirectional(this.vineyard.nameProperty());
			if (this.vineyard.getAddress() != null)
				fieldAddress.textProperty().unbindBidirectional(this.vineyard.getAddress().addressProperty());
		}
		
		if (vineyard != null)
			this.vineyard = vineyard;
		else {
			this.vineyard = new Vineyard();
			this.vineyard.setName("");
			this.vineyard.setAddress(new Address());
			this.vineyard.getAddress().setAddress("");
		}
		
		fieldName.textProperty().bindBidirectional(this.vineyard.nameProperty());
		fieldAddress.textProperty().bindBidirectional(this.vineyard.getAddress().addressProperty());
		
		labelFormVineyard.setText(vineyard != null ? "Edit vineyard" : "Create vineyard");
		buttonDelete.setVisible(vineyard != null);
		buttonCancel.setVisible(vineyard != null);
	}

	@SuppressWarnings("unused")
	@FXML
	private void save(ActionEvent event) {
		final boolean isNew = vineyard.getId() == null;
		vineyardRepository.save(vineyard, 
			new SimpleTideResponder<Vineyard>() {
				@Override
				public void result(TideResultEvent<Vineyard> tre) {
					if (isNew)
						select(null);
					else
						tableVineyards.getSelectionModel().clearSelection();
				}
				
				@Override
				public void fault(TideFaultEvent tfe) {
					System.out.println("Error: " + tfe.getFault().getFaultDescription());
				}
			}
		);
	}

	@SuppressWarnings("unused")
	@FXML
	private void delete(ActionEvent event) {
		vineyardRepository.delete(vineyard.getId(), 
			new SimpleTideResponder<Void>() {
				@Override
				public void result(TideResultEvent<Void> tre) {
					tableVineyards.getSelectionModel().clearSelection();
				}
			}
		);
	}

	@SuppressWarnings("unused")
	@FXML
	private void cancel(ActionEvent event) {
		tableVineyards.getSelectionModel().clearSelection();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		vineyards.getFilter().nameProperty().bindBidirectional(fieldSearch.textProperty());
		vineyards.setSort(new TableViewSort<Vineyard>(tableVineyards, Vineyard.class));

		select(null);
		tableVineyards.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Vineyard>() {
			@Override
			public void changed(ObservableValue<? extends Vineyard> property, Vineyard oldSelection, Vineyard newSelection) {
				select(newSelection);
			}			
		});
	}
}
