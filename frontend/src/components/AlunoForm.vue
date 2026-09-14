<template>
  <div class="card">
    <h2>Cadastrar Aluno</h2>
    <form @submit.prevent="criarAluno">
      <div class="form-group">
        <label>Nome</label>
        <input v-model="form.nome" placeholder="Nome do aluno" required />
      </div>
      <div class="form-group">
        <label>Email</label>
        <input v-model="form.email" type="email" placeholder="email@exemplo.com" required />
      </div>
      <button type="submit" class="btn btn-primary" :disabled="loading">
        {{ loading ? 'Salvando...' : 'Cadastrar' }}
      </button>
      <div v-if="msg" :class="['alert', erro ? 'alert-error' : 'alert-success']">{{ msg }}</div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['aluno-criado'])

const form = ref({ nome: '', email: '' })
const loading = ref(false)
const msg = ref('')
const erro = ref(false)

async function criarAluno() {
  loading.value = true
  msg.value = ''
  try {
    const res = await fetch('/api/forum-rewards/alunos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    })
    if (!res.ok) throw new Error('Erro ao cadastrar')
    form.value = { nome: '', email: '' }
    msg.value = 'Aluno cadastrado com sucesso!'
    erro.value = false
    emit('aluno-criado')
  } catch (e) {
    msg.value = 'Erro ao cadastrar aluno.'
    erro.value = true
  } finally {
    loading.value = false
  }
}
</script>
