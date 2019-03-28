package database;


public class Product {
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreation_time() {
		return creation_time;
	}
	public void setCreation_time(String creation_time) {
		this.creation_time = creation_time;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String UUID) {
		this.UUID = UUID;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getMaterial() {
		return material;
	}
	
	private int id;
	private String creation_time;

	private int status;
	private Float price;
	private String item;
	private String UUID;
	
	private String material;
}
