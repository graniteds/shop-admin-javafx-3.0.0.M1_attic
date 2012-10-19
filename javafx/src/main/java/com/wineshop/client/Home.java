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
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import javax.inject.Inject;

import org.granite.client.tide.collections.javafx.PagedQuery;
import org.granite.client.tide.collections.javafx.TableViewSort;
import org.granite.client.tide.data.EntityManager;
import org.granite.client.tide.javafx.JavaFXDataManager;
import org.granite.client.tide.server.SimpleTideResponder;
import org.granite.client.tide.server.TideFaultEvent;
import org.granite.client.tide.server.TideResultEvent;
import org.granite.client.validation.javafx.FormValidator;
import org.granite.client.validation.javafx.ValidationResultEvent;
import org.springframework.stereotype.Component;

import com.wineshop.client.entities.Address;
import com.wineshop.client.entities.Vineyard;
import com.wineshop.client.entities.Wine;
import com.wineshop.client.entities.Wine$Type;
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
	private Parent formVineyard;
	
	@FXML
	private TextField fieldName;
	
	@FXML
	private TextField fieldAddress;
	
	@FXML
	private ListView<Wine> listWines;

	@FXML
	private Button buttonSave;

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
	
	@Inject
	private EntityManager entityManager;
	
	@Inject
	private JavaFXDataManager dataManager;
	
	private FormValidator formValidator = new FormValidator();
	
	
	@SuppressWarnings("unused")
	@FXML
	private void search(ActionEvent event) {
		vineyards.refresh();
	}
	
	
	private void select(Vineyard vineyard) {
		if (vineyard == this.vineyard && this.vineyard != null)
			return;
		
		formValidator.setForm(null);
		
		if (this.vineyard != null) {
			fieldName.textProperty().unbindBidirectional(this.vineyard.nameProperty());
			fieldAddress.textProperty().unbindBidirectional(this.vineyard.getAddress().addressProperty());
			entityManager.resetEntity(this.vineyard);
		}
		
		if (vineyard != null)
			this.vineyard = vineyard;
		else {
			this.vineyard = new Vineyard();
			this.vineyard.setName("");
			this.vineyard.setAddress(new Address());
			this.vineyard.getAddress().setAddress("");
			entityManager.mergeExternalData(this.vineyard);
		}
		
		fieldName.textProperty().bindBidirectional(this.vineyard.nameProperty());
		fieldAddress.textProperty().bindBidirectional(this.vineyard.getAddress().addressProperty());
		listWines.setItems(this.vineyard.getWines());
		
		formValidator.setForm(formVineyard);
		
		labelFormVineyard.setText(vineyard != null ? "Edit vineyard" : "Create vineyard");
		buttonDelete.setVisible(vineyard != null);
		buttonCancel.setVisible(vineyard != null);
	}

	@SuppressWarnings("unused")
	@FXML
	private void save(ActionEvent event) {
		if (!formValidator.validate(this.vineyard))
			return;
		
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
	
	@SuppressWarnings("unused")
	@FXML
	private void addWine(ActionEvent event) {
		Wine wine = new Wine();
		wine.setVineyard(this.vineyard);
		wine.setName("");
		wine.setYear(Calendar.getInstance().get(Calendar.YEAR)-3);
		wine.setType(Wine$Type.RED);
		this.vineyard.getWines().add(wine);
	}
	
	@SuppressWarnings("unused")
	@FXML
	private void removeWine(ActionEvent event) {
		if (!listWines.getSelectionModel().isEmpty())
			this.vineyard.getWines().remove(listWines.getSelectionModel().getSelectedIndex());
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
		
		listWines.setCellFactory(new Callback<ListView<Wine>, ListCell<Wine>>() {
			public ListCell<Wine> call(ListView<Wine> listView) {
				return new WineListCell();
			}
		});
		
		buttonSave.disableProperty().bind(Bindings.not(dataManager.dirtyProperty()));
		
		formVineyard.addEventHandler(ValidationResultEvent.ANY, new EventHandler<ValidationResultEvent>() {
			@Override
			public void handle(ValidationResultEvent event) {
				if (event.getEventType() == ValidationResultEvent.INVALID)
					((Node)event.getTarget()).setStyle("-fx-border-color: red");
				else if (event.getEventType() == ValidationResultEvent.VALID)
					((Node)event.getTarget()).setStyle("-fx-border-color: null");
			}
		});
	}
	
	
	private static class WineListCell extends ListCell<Wine> {
		
		private ChoiceTypeListener choiceTypeListener = null;
		
		protected void updateItem(Wine wine, boolean empty) {
			Wine oldWine = getItem();
			if (oldWine != null && wine == null) {
				HBox hbox = (HBox)getGraphic();
				
				TextField fieldName = (TextField)hbox.getChildren().get(0);
				fieldName.textProperty().unbindBidirectional(getItem().nameProperty());
				
				TextField fieldYear = (TextField)hbox.getChildren().get(1);
				fieldYear.textProperty().unbindBidirectional(getItem().yearProperty());
				
				getItem().typeProperty().unbind();
				getItem().typeProperty().removeListener(choiceTypeListener);
				choiceTypeListener = null;
				
				setGraphic(null);
			}
			
			super.updateItem(wine, empty);
			
			if (wine != null && wine != oldWine) {
				TextField fieldName = new TextField();
				fieldName.textProperty().bindBidirectional(wine.nameProperty());
				
				TextField fieldYear = new TextField();
				fieldYear.setPrefWidth(40);
				fieldYear.textProperty().bindBidirectional(wine.yearProperty(), new IntegerStringConverter());
				
				ChoiceBox<Wine$Type> choiceType = new ChoiceBox<Wine$Type>(FXCollections.observableArrayList(Wine$Type.values()));
				choiceType.getSelectionModel().select(getItem().getType());
				getItem().typeProperty().bind(choiceType.getSelectionModel().selectedItemProperty());
				choiceTypeListener = new ChoiceTypeListener(choiceType);
				getItem().typeProperty().addListener(choiceTypeListener);
				
				HBox hbox = new HBox();
				hbox.setSpacing(5.0);
				hbox.getChildren().add(fieldName);
				hbox.getChildren().add(fieldYear);
				hbox.getChildren().add(choiceType);
				setGraphic(hbox);
			}
		}
		
		private final static class ChoiceTypeListener implements ChangeListener<Wine$Type> {
			private ChoiceBox<Wine$Type> choiceBox;
			
			public ChoiceTypeListener(ChoiceBox<Wine$Type> choiceBox) {
				this.choiceBox = choiceBox;
			}
			
			@Override
			public void changed(ObservableValue<? extends Wine$Type> property,
					Wine$Type oldValue, Wine$Type newValue) {
				choiceBox.getSelectionModel().select(newValue);
			}
		}
	}
}
