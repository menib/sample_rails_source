class PetsController < ApplicationController
  def create
    @pet = Pet.new(pet_params)
 
    @pet.save
    redirect_to @pet
  end

  def show
    @pet = Pet.find(params[:id])
  end
 
  def new
  end

  def index
    @pets = Pet.all
  end

private
  def pet_params
    params.require(:pet).permit(:name, :breed, :gender)
  end
end
