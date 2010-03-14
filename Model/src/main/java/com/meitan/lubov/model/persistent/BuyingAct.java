package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.Price;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Date: Jan 27, 2010
 * Time: 8:20:33 PM
 *
 * @author denisk
 */
@Entity
@Table(name = "buying_act")
public class BuyingAct implements Cloneable {
	private long id;
	private Date date;
	private Client client;
	private Set<Product> products = new HashSet<Product>();
	private Price totalPrice;

	public BuyingAct() {
	}

	public BuyingAct(Date date, Client client) {
		this.date = date;
		this.client = client;
	}

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	//todo this shouldn't be null in the DB. Add constraint
	@ManyToOne(targetEntity = Client.class, optional = false)
	@JoinColumn(name="client_id")
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	@ManyToMany(mappedBy = "purchases")
	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@Embedded
	public Price getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Price totalPrice) {
		this.totalPrice = totalPrice;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuyingAct)) return false;

        BuyingAct act = (BuyingAct) o;

        if (client != null ? !client.equals(act.client) : act.client != null) return false;
        if (date != null ? !Long.valueOf(date.getTime()).equals(act.date.getTime()) : act.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (client != null ? client.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "BuyingAct{" + "id=" + id + ", date=" + date + ", products=" + products + ", totalPrice=" + totalPrice + '}';
	}
}
