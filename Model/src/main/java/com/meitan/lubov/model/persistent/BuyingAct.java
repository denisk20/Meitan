package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.IdAware;
import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Price;
import com.meitan.lubov.model.util.PersistentOrderableImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import javax.persistence.*;

/**
 * Date: Jan 27, 2010
 * Time: 8:20:33 PM
 *
 * @author denisk
 */
@Entity
@Table(name = "buying_act")
@NamedQueries({
		@NamedQuery(name = "findForCartItem", query = "select b from BuyingAct b inner join b.products i where i.id = :itemId"),
		@NamedQuery(name = "findForLogin", query = "from BuyingAct b where b.client.login = :login")
})
public class BuyingAct extends PersistentOrderableImpl implements Cloneable, Serializable, IdAware {
	private long id;
	private Date date;
	private Client client;
	//todo rename this
	private Set<ShoppingCartItem> products = new HashSet<ShoppingCartItem>();

	public BuyingAct() {
	}

	public BuyingAct(Date date, Client client) {
		this.date = date;
		this.client = client;
	}

	public BuyingAct(Date date, Client client, Set<ShoppingCartItem> products) {
		this.date = date;
		this.client = client;
		this.products = products;
	}

	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	@ManyToOne(targetEntity = Client.class, optional = false)
	@JoinColumn(name="client_id")
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	//TODO RENAME THIS
	@OneToMany
	public Set<ShoppingCartItem> getProducts() {
		return products;
	}

	public void setProducts(Set<ShoppingCartItem> products) {
		this.products = products;
	}

	@Transient
	public Price getTotalPrice() {
		BigDecimal result = new BigDecimal(0);
		for (ShoppingCartItem item : products) {
			result = result.add(item.getPrice());
		}
		return new Price(result);
	}

	@Transient
	public HashSet<PriceAware> getProductsSet() {
		HashSet<PriceAware> result = new HashSet<PriceAware>();
		for (ShoppingCartItem item : products) {
			result.add(item.getItem());
		}
		return result;
	}

	@Transient
	public List<ShoppingCartItem> getProductsList() {
		return new ArrayList<ShoppingCartItem>(products);
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
		return "BuyingAct{" + "id=" + id + ", date=" + date + ", products=" + products + '}';
	}
}
